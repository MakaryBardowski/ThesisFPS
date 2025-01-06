package game.effects.particleStrategies;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import game.items.Item;

public class DroppedItem extends ParticleMovementStrategy {

    private final Vector3f velocity;
    private final Vector3f rotationVelocity;
    private final float finalY;
    private final Item item;
    private static final float GRAVITY = 13.81f;//9.81f;

    public DroppedItem(Node node, Vector3f velocity, Vector3f rotationVelocity, float finalY, Item item) {
        super(node);
        this.velocity = velocity;
        this.rotationVelocity = rotationVelocity;
        this.finalY = finalY;
        this.item = item;
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
        node.getChild(1).getWorldRotation().toAngles(startQ);
        startQ[0] = item.getTemplate().getDropData().getRotation().getX();
        startQ[2] = item.getTemplate().getDropData().getRotation().getZ();
        node.getChild(1).setLocalRotation(new Quaternion().fromAngles(startQ));
        node.getWorldTranslation().setY(finalY).addLocal(item.getTemplate().getDropData().getOffset());
        removeControl();
    }

    @Override
    protected boolean hasNotHitGround() {
        return node.getLocalTranslation().getY() > finalY;
    }

    @Override
    public void move(float tpf) {
        node.getChild(1).rotate(rotationVelocity.x * tpf, rotationVelocity.y * tpf, rotationVelocity.z * tpf);
        node.move(velocity.mult(tpf));
        velocity.subtractLocal(0, GRAVITY * tpf, 0);
    }
    
}
