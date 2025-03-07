package game.effects.particleStrategies;

import client.appStates.ClientGameAppState;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.Random;
import static game.effects.DecalProjector.projectFromTo;

public class GoreParticle extends ParticleMovementStrategy {

    private final Vector3f velocity;
    private final Vector3f rotationVelocity;
    private final float finalY;
    private static final float GRAVITY = 13.81f;//9.81f;

    public GoreParticle(Node node, Vector3f velocity, Vector3f rotationVelocity, float finalY) {
        super(node);
        this.velocity = velocity;
        this.rotationVelocity = rotationVelocity;
        this.finalY = finalY;
    }

    @Override
    public void updateParticle(float tpf) {
        if (hasNotHitGround()) {
            move(tpf);
        } else {
            onHitGround();
        }
    }

    @Override
    public void onHitGround() {
        float[] startQ = new float[3];
        node.getWorldRotation().toAngles(startQ);
        startQ[0] = 0;
        startQ[2] = 0;
        node.setLocalRotation(new Quaternion().fromAngles(startQ));
        node.getWorldTranslation().setY(finalY);
        projectFromTo(ClientGameAppState.getInstance(), node.getWorldTranslation().clone().add(0, 1, 0), new Vector3f(0, -1, 0),"Textures/Gameplay/Decals/testBlood" + new Random().nextInt(2) + ".png",new Random().nextInt(2)+2f);
        removeControl();
    }

    @Override
    public void move(float tpf) {
        node.rotate(rotationVelocity.x * tpf, rotationVelocity.y * tpf, rotationVelocity.z * tpf);
        node.move(velocity.mult(tpf));
        velocity.subtractLocal(0, GRAVITY * tpf, 0);
    }

    @Override
    protected boolean hasNotHitGround() {
        return node.getLocalTranslation().getY() > finalY;
    }

}
