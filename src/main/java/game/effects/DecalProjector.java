package game.effects;

import client.appStates.ClientGameAppState;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix4f;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Transform;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.texture.Texture;
import com.jme3.util.BufferUtils;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DecalProjector {

    private List<Geometry> geometries;
    private Vector3f size;
    private Matrix4f projectorMatrix;
    private Matrix4f projectorMatrixInverse;
    private float separation;
    DecalVertex nV1;
    DecalVertex nV2;
    DecalVertex nV3;
    DecalVertex nV4;
    Vector2f[] decalUvs;
    Vector3f[] decalPositions;
    Vector3f[] decalNormals;
    int[] decalIndices;
    private Vector3f position;
    private Vector3f normal;
    private static final Vector3f[] planes = new Vector3f[]{
        new Vector3f(1, 0, 0),
        new Vector3f(-1, 0, 0),
        new Vector3f(0, 1, 0),
        new Vector3f(0, -1, 0),
        new Vector3f(0, 0, 1),
        new Vector3f(0, 0, -1)
    };

    public DecalProjector(Spatial spatial, Vector3f position, Quaternion rotation, Vector3f size) {
        var geometries = new ArrayList<Geometry>();
        spatial.depthFirstTraversal(s -> {
            if (s instanceof Geometry) {
                geometries.add((Geometry) s);
            }
        });
        setGeometries(geometries);
        setSize(size);
        setSeparation(0.0001f);
        setTransform(new Transform(position, rotation, new Vector3f(1, 1, 1)));
    }

    public DecalProjector(Collection<Geometry> geometries, Vector3f position, Quaternion rotation, Vector3f size) {
        this(geometries, position, rotation, size, 0.0001f);
    }

    public DecalProjector(Collection<Geometry> geometries, Vector3f position, Quaternion rotation, Vector3f size,
            float separation) {
        setSize(size);
        setGeometries(geometries);
        setSeparation(separation);
        setTransform(new Transform(position, rotation, new Vector3f(1, 1, 1)));
    }

    public void setSize(Vector3f size) {
        this.size = size;
    }

    public void setGeometries(Collection<Geometry> geometries) {
        this.geometries = List.copyOf(geometries);
    }

    public void setSeparation(float separation) {
        this.separation = separation;
    }

    public void setTransform(Transform transform) {
        projectorMatrix = transform.toTransformMatrix();
        projectorMatrixInverse = projectorMatrix.invert();
    }

    public Geometry project() {
        // first, create an array of 'DecalVertex' objects
        // three consecutive 'DecalVertex' objects represent a single face
        // this data structure will be later used to perform the clipping
        var decalVertices = new ArrayList<DecalVertex>();

        for (var geometry : geometries) {
//        System.out.println("geometry "+geometry);
            geometry.computeWorldMatrix();
            var mesh = geometry.getMesh();
            var positions = getVectors(mesh, VertexBuffer.Type.Position);
            var normals = getVectors(mesh, VertexBuffer.Type.Normal);
            var indices = mesh.getIndexBuffer();

            for (var i = 0; i < indices.size(); i++) {
                var index = indices.get(i);
                pushDecalVertex(geometry, decalVertices, positions[index], normals[index]);
            }
        }

        // second, clip the geometry so that it doesn't extend out from the projector
        decalVertices = clipVertices(decalVertices);

        // third, generate final vertices, normals and uvs
        decalUvs = new Vector2f[decalVertices.size()];
        decalPositions = new Vector3f[decalVertices.size()];
        decalNormals = new Vector3f[decalVertices.size()];
        decalIndices = new int[3 * decalVertices.size()];
        var i = 0;

        for (var decalVertex : decalVertices) {
            // create texture coordinates (we are still in projector space)
            decalUvs[i] = new Vector2f(0.5f + (decalVertex.position.x / size.x), 0.5f + (decalVertex.position.y / size.y));

            // transform the vertex back to world space
            projectorMatrix.mult(decalVertex.position, decalVertex.position);

            // now create vertex and normal buffer data
            decalPositions[i] = decalVertex.position;
            decalNormals[i] = decalVertex.normal;
            decalIndices[i] = i++;
        }

        var decalMesh = new Mesh();
        decalMesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(decalPositions));
        decalMesh.setBuffer(Type.Index, 1, BufferUtils.createIntBuffer(decalIndices));
        decalMesh.setBuffer(Type.Normal, 3, BufferUtils.createFloatBuffer(decalNormals));
        decalMesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(decalUvs));
        decalMesh.updateBound();

        return new Geometry("decal", decalMesh);
    }

    protected ArrayList<DecalVertex> clipVertices(ArrayList<DecalVertex> decalVertices) {
        decalVertices = clipGeometry(decalVertices, planes[0]);
        decalVertices = clipGeometry(decalVertices, planes[1]);
        decalVertices = clipGeometry(decalVertices, planes[2]);
        decalVertices = clipGeometry(decalVertices, planes[3]);
        decalVertices = clipGeometry(decalVertices, planes[4]);
        decalVertices = clipGeometry(decalVertices, planes[5]);
        return decalVertices;
    }

    private void pushDecalVertex(Geometry geometry, List<DecalVertex> vertices, Vector3f pos, Vector3f n) {
         position = pos.clone();
         normal = n;

        // move the vertex away from the original (to avoid z-fighting)
        position.addLocal(normal.mult(separation));
        // transform the vertex to world space, then to projector space
        geometry.getWorldMatrix().mult(position, position);
        projectorMatrixInverse.mult(position, position);

        geometry.getWorldMatrix().rotateVect(normal);
        vertices.add(new DecalVertex(position, normal));
    }

    private ArrayList<DecalVertex> clipGeometry(List<DecalVertex> inVertices, Vector3f plane) {
        var outVertices = new ArrayList<DecalVertex>();
        var s = 0.5f * FastMath.abs(size.dot(plane));

        // a single iteration clips one face,
        // which consists of three consecutive 'DecalVertex' objects
        for (var i = 0; i < inVertices.size(); i += 3) {

            var total = 0;
            var d1 = inVertices.get(i + 0).position.dot(plane) - s;
            var d2 = inVertices.get(i + 1).position.dot(plane) - s;
            var d3 = inVertices.get(i + 2).position.dot(plane) - s;
            var v1Out = d1 > 0;
            var v2Out = d2 > 0;
            var v3Out = d3 > 0;

            // calculate, how many vertices of the face lie outside of the clipping plane
            total = (v1Out ? 1 : 0) + (v2Out ? 1 : 0) + (v3Out ? 1 : 0);
            switch (total) {
                case 0: {
                    // the entire face lies inside of the plane, no clipping needed
                    outVertices.add(inVertices.get(i));
                    outVertices.add(inVertices.get(i + 1));
                    outVertices.add(inVertices.get(i + 2));
                    break;
                }

                case 1: {
                    // one vertex lies outside of the plane, perform clipping
                    if (v1Out) {
                        nV1 = inVertices.get(i + 1);
                        nV2 = inVertices.get(i + 2);
                        nV3 = clip(inVertices.get(i), nV1, plane, s);
                        nV4 = clip(inVertices.get(i), nV2, plane, s);
                    }
                    if (v2Out) {
                        nV1 = inVertices.get(i);
                        nV2 = inVertices.get(i + 2);
                        nV3 = clip(inVertices.get(i + 1), nV1, plane, s);
                        nV4 = clip(inVertices.get(i + 1), nV2, plane, s);

                        outVertices.add(nV3);
                        outVertices.add(nV2.clone());
                        outVertices.add(nV1.clone());

                        outVertices.add(nV2.clone());
                        outVertices.add(nV3.clone());
                        outVertices.add(nV4);
                        break;
                    }
                    if (v3Out) {
                        nV1 = inVertices.get(i);
                        nV2 = inVertices.get(i + 1);
                        nV3 = clip(inVertices.get(i + 2), nV1, plane, s);
                        nV4 = clip(inVertices.get(i + 2), nV2, plane, s);
                    }

                    outVertices.add(nV1.clone());
                    outVertices.add(nV2.clone());
                    outVertices.add(nV3);

                    outVertices.add(nV4);
                    outVertices.add(nV3.clone());
                    outVertices.add(nV2.clone());
                    break;
                }
                case 2: {
                    // two vertices lies outside of the plane, perform clipping
                    if (!v1Out) {
                        nV1 = inVertices.get(i).clone();
                        nV2 = clip(nV1, inVertices.get(i + 1), plane, s);
                        nV3 = clip(nV1, inVertices.get(i + 2), plane, s);
                        outVertices.add(nV1);
                        outVertices.add(nV2);
                        outVertices.add(nV3);
                    }
                    if (!v2Out) {
                        nV1 = inVertices.get(i + 1).clone();
                        nV2 = clip(nV1, inVertices.get(i + 2), plane, s);
                        nV3 = clip(nV1, inVertices.get(i), plane, s);
                        outVertices.add(nV1);
                        outVertices.add(nV2);
                        outVertices.add(nV3);
                    }
                    if (!v3Out) {
                        nV1 = inVertices.get(i + 2).clone();
                        nV2 = clip(nV1, inVertices.get(i), plane, s);
                        nV3 = clip(nV1, inVertices.get(i + 1), plane, s);
                        outVertices.add(nV1);
                        outVertices.add(nV2);
                        outVertices.add(nV3);
                    }
                    break;
                }
                case 3: {
                    // the entire face lies outside of the plane, so let's discard the corresponding
                    // vertices
                    break;
                }

            }
        }
        return outVertices;
    }

    private DecalVertex clip(DecalVertex v0, DecalVertex v1, Vector3f p, float s) {
        var d0 = v0.position.dot(p) - s;
        var d1 = v1.position.dot(p) - s;

        var s0 = d0 / (d0 - d1);

        // improves performance for some reason
        var v = new DecalVertex(
                new Vector3f(v0.position.x + s0 * (v1.position.x - v0.position.x),
                        v0.position.y + s0 * (v1.position.y - v0.position.y), v0.position.z + s0 * (v1.position.z - v0.position.z)),
                new Vector3f(v0.normal.x + s0 * (v1.normal.x - v0.normal.x), v0.normal.y + s0 * (v1.normal.y - v0.normal.y),
                        v0.normal.z + s0 * (v1.normal.z - v0.normal.z)));
        // need to clip more values (texture coordinates)? do it this way:
        // intersectpoint.value = a.value + s * ( b.value - a.value );
        return v;
    }

    private Vector3f[] getVectors(Mesh mesh, Type bufferType) {
        var buffer = mesh.getBuffer(bufferType);
        var data = (FloatBuffer) buffer.getDataReadOnly();
        return BufferUtils.getVector3Array(data);
    }



    protected class DecalVertex {

        private final Vector3f position;
        private final Vector3f normal;

        private DecalVertex(Vector3f p, Vector3f n) {
            this.position = p;
            this.normal = n;
        }

        public DecalVertex clone() {
            return new DecalVertex(position.clone(), normal.clone());
        }
    }
    
    
        public static void projectFromTo(ClientGameAppState gs, Vector3f from, Vector3f to, String texturePath,float projectionBoxSize) {
        for (int i = 0; i < 1; i++) {

            var contactFaceNormal = new Vector3f(); // Get the normal vector of the contact face
            CollisionResults results = new CollisionResults();
            Ray ray = new Ray(from, to);
            gs.getMapNode().collideWith(ray, results);
            var contactPoint = new Vector3f();

            if (results.size() > 0) {
                CollisionResult closest = results.getClosestCollision();
                contactFaceNormal = closest.getContactNormal();
                contactPoint = closest.getContactPoint();
            }

            var dist = 0.1f;
            var pos = contactPoint.add(contactFaceNormal.mult(dist / 2));
//            var pos = contactPoint.add(contactFaceNormal.mult(0.1f));
            var rot = new Quaternion().lookAt(contactFaceNormal.negate(), Vector3f.UNIT_Y);
            pos.addLocal(rot.getRotationColumn(2).mult(dist / 2));
            var projectionBox = new Vector3f(projectionBoxSize, projectionBoxSize, dist);

            var projector = new DecalProjector(gs.getMapNode(), pos, rot, projectionBox);
            projector.setSeparation(0.002f);
            var geometry = projector.project();
            Texture texture = gs.getAssetManager().loadTexture(texturePath);
            texture.setMagFilter(Texture.MagFilter.Nearest);
            Material material = new Material(gs.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
            material.setTexture("ColorMap", texture);
            material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
            material.getAdditionalRenderState().setDepthWrite(false);
            material.getAdditionalRenderState().setPolyOffset(-4, -1f);
            // Apply the material to the geometry
            geometry.setMaterial(material);
            geometry.setQueueBucket(RenderQueue.Bucket.Transparent);

            geometry.setMaterial(material);

            gs.getDebugNode().attachChild(geometry);
//            var box = new Geometry("box", new Box(projectionBox.x / 2f, projectionBox.y / 2f, projectionBox.z / 2f));
//            var boxmat = new Material(gs.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
//            boxmat.getAdditionalRenderState().setWireframe(true);
//            boxmat.setColor("Color", ColorRGBA.White);
//            box.setMaterial(boxmat);
//            box.setLocalTranslation(pos);
//            box.setLocalRotation(rot);
//            gs.getRootNode().attachChild(box);
        }

    }
}
