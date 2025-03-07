package game.entities;

import client.appStates.ClientGameAppState;
import static client.appStates.ClientGameAppState.removeEntityByIdClient;
import client.Main;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.scene.Node;
import game.entities.DecorationTemplates.DecorationTemplate;
import game.map.collision.RectangleAABB;
import game.map.collision.WorldGrid;
import lombok.Getter;
import messages.NewIndestructibleDecorationMessage;
import server.ServerGameAppState;
import static server.ServerGameAppState.removeEntityByIdServer;

public class IndestructibleDecoration extends Collidable {

    @Getter
    protected DecorationTemplate template;

    public IndestructibleDecoration(int id, String name, Node node, DecorationTemplate template) {
        super(id, name, node);
        this.template = template;
        createHitbox();
    }

    @Override
    protected final void createHitbox() {
        float hitboxWidth = template.getCollisionShapeWidth();
        float hitboxHeight = template.getCollisionShapeHeight();
        float hitboxLength = template.getCollisionShapeLength();
        hitboxNode.move(0, hitboxHeight, 0);
        collisionShape = new RectangleAABB(hitboxNode.getWorldTranslation(), hitboxWidth, hitboxHeight, hitboxLength);
        showHitboxIndicator();
    }

    @Override
    public void onInteract() {
    }

    @Override
    public void setPositionClient(Vector3f newPos) {
        WorldGrid grid = ServerGameAppState.getInstance().getGrid();
        grid.remove(this);
        node.setLocalTranslation(newPos);
        grid.insert(this);
    }

    @Override
    public void setPositionServer(Vector3f newPos) {
        WorldGrid grid = ServerGameAppState.getInstance().getGrid();
        grid.remove(this);
        node.setLocalTranslation(newPos);
        grid.insert(this);
    }

    @Override
    public AbstractMessage createNewEntityMessage() {
        var msg = new NewIndestructibleDecorationMessage(this);
        msg.setReliable(true);
        return msg;
    }

    @Override
    public void onCollisionClient(Collidable other) {
    }

    @Override
    public void onCollisionServer(Collidable other) {
    }

    @Override
    public void moveClient(float tpf) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void destroyServer() {
        var server = ServerGameAppState.getInstance();
        server.getGrid().remove(this);
        if (node.getParent() != null) {
            Main.getInstance().enqueue(() -> {
                node.removeFromParent();
            });
        }
        removeEntityByIdServer(id);
    }

    @Override
    public void destroyClient() {
        var client = ClientGameAppState.getInstance();
        client.getGrid().remove(this);
        Main.getInstance().enqueue(() -> {
            node.removeFromParent();
        });
        removeEntityByIdClient(id);
    }

}
