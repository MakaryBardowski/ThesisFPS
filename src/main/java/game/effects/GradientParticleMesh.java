package game.effects;

import com.jme3.effect.Particle;
import com.jme3.math.Matrix3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Mesh;

public abstract class GradientParticleMesh extends Mesh {

    public enum Type {
        Point,

        Triangle;
    }

    public abstract void initParticleData(GradientParticleEmitter emitter, int numParticles);

    public abstract void setImagesXY(int imagesX, int imagesY);

    public abstract void updateParticleData(Particle[] particles, Camera cam, Matrix3f inverseRotation);

}