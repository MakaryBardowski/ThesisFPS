package game.items;

import client.appStates.ClientGameAppState;
import static client.appStates.ClientGameAppState.removeEntityByIdClient;
import client.Main;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import static debugging.DebugUtils.createUnshadedBoxNode;

import game.entities.AttachedEntity;
import game.effects.ParticleUtils;

import static game.entities.DestructibleUtils.setupModelShootability;

import game.items.ItemTemplates.ItemTemplate;
import lombok.Getter;

import java.util.concurrent.atomic.AtomicBoolean;

import static server.ServerGameAppState.removeEntityByIdServer;

@Getter
public abstract class Item extends AttachedEntity {

    protected boolean droppable;
    protected String description;
    protected ItemTemplate template;
    protected Node droppedItemNode;

    protected AtomicBoolean droppedOnServer = new AtomicBoolean(false);

    protected Item(int id, ItemTemplate template, String name, Node node) {
        super(id, name, node);
        this.template = template;
        this.droppable = true;
    }

    protected Item(int id, ItemTemplate template, String name, Node node, boolean droppable) {
        super(id, name, node);
        this.template = template;
        this.droppable = droppable;
    }

    public void drop(Vector3f itemSpawnpoint) {
        if (!droppable) {
            return;
        }

        Node parentNode = createUnshadedBoxNode();
        var invisibleHitbox = parentNode.getChild("Box");
        invisibleHitbox.scale(2);
        invisibleHitbox.setCullHint(Spatial.CullHint.Always);
        setupModelShootability(parentNode, id);
        setupModelShootability(node, id);

        applyInitialDropRotation(node);
        parentNode.attachChild(node);
        parentNode.scale(template.getDropData().getScale());
        parentNode.setLocalTranslation(itemSpawnpoint);
        droppedItemNode = parentNode;
        ClientGameAppState.getInstance().getPickableNode().attachChild(parentNode);
        ParticleUtils.spawnStaticItemPhysicalParticleShaded(parentNode, itemSpawnpoint, this);
    }

    public void drop(Vector3f itemSpawnpoint, Vector3f dropVelocity) {
        if (!droppable) {
            return;
        }

        Node parentNode = createUnshadedBoxNode();
        var invisibleHitbox = parentNode.getChild("Box");
        invisibleHitbox.scale(2);
        invisibleHitbox.setCullHint(Spatial.CullHint.Always);
        setupModelShootability(parentNode, id);
        setupModelShootability(node, id);

        applyInitialDropRotation(node);
        parentNode.attachChild(node);
        parentNode.scale(template.getDropData().getScale());
        parentNode.setLocalTranslation(itemSpawnpoint);
        droppedItemNode = parentNode;
        ClientGameAppState.getInstance().getPickableNode().attachChild(parentNode);
        ParticleUtils.spawnItemPhysicalParticleShadedWithVelocity(parentNode, itemSpawnpoint, this, dropVelocity);
    }

    private void applyInitialDropRotation(Node childNode) {
        Vector3f dr = template.getDropData().getRotation();
        float[] angles = {dr.getX(), dr.getY(), dr.getZ()};
        childNode.setLocalRotation(new Quaternion().fromAngles(angles));
    }

    @Override
    public void setPositionClient(Vector3f newPos) {
        if (droppedItemNode != null) {
            droppedItemNode.setLocalTranslation(newPos);
        } else {
            throw new IllegalStateException("the " + this + " cannot be moved - it is not on the ground!");
        }
    }

    @Override
    public void setPositionServer(Vector3f newPos) {
        throw new StackOverflowError("item.setPositionServer not supported");
    }

    public abstract String getDescription();

    @Override
    public void destroyServer() {
        // does it ever execute on server?
        if (droppedItemNode != null && droppedItemNode.getParent() != null) {
            Main.getInstance().enqueue(() -> {
                droppedItemNode.removeFromParent();
            });
        }
        //
        removeEntityByIdServer(id);
    }

    @Override
    public void destroyClient() {
        if (droppedItemNode != null && node.getParent() != null) {
            Main.getInstance().enqueue(() -> {
                droppedItemNode.removeFromParent();
            });
        }
        removeEntityByIdClient(id);
    }

    public void setDroppedOnServer(boolean value){
        droppedOnServer.set(value);
    }

    public boolean isDroppedOnServer(){
        return droppedOnServer.get();
    }

}
