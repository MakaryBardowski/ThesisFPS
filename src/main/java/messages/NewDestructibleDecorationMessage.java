package messages;

import client.appStates.ClientGameAppState;
import com.jme3.math.Vector3f;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import game.entities.DecorationTemplates;
import game.entities.DestructibleDecoration;
import game.entities.factories.DecorationFactory;
import lombok.Getter;
import server.ServerGameAppState;

@Serializable
public class NewDestructibleDecorationMessage extends TwoWayMessage {

    @Getter
    protected int id;
    protected int templateIndex;
    @Getter
    private float health;
    protected float x;
    protected float y;
    protected float z;

    public NewDestructibleDecorationMessage() {
    }

    public NewDestructibleDecorationMessage(DestructibleDecoration decoration) {
        this.id = decoration.getId();
        this.templateIndex = decoration.getTemplate().getTemplateIndex();
        this.health = decoration.getHealth();
        this.x = decoration.getNode().getWorldTranslation().getX();
        this.y = decoration.getNode().getWorldTranslation().getY();
        this.z = decoration.getNode().getWorldTranslation().getZ();

    }

    public DecorationTemplates.DecorationTemplate getTemplate() {
        return DecorationTemplates.getTemplateByIndex(templateIndex);
    }

    public Vector3f getPos() {
        return new Vector3f(x, y, z);
    }

    @Override
    public void handleServer(ServerGameAppState server, HostedConnection hc) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        addNewDestructibleDecoration(this);
    }

    private void addNewDestructibleDecoration(NewDestructibleDecorationMessage nmsg) {

        if (entityNotExistsLocallyClient(nmsg.getId())) {
            enqueueExecution(() -> {
                DestructibleDecoration d = DecorationFactory.createDestructibleDecoration(nmsg.getId(), ClientGameAppState.getInstance().getDestructibleNode(), nmsg.getPos(), nmsg.getTemplate(), ClientGameAppState.getInstance().getAssetManager());
                ClientGameAppState.getInstance().getMobs().put(d.getId(), d);
                d.setHealth(nmsg.getHealth());
                ClientGameAppState.getInstance().getGrid().insert(d);
            });
        }
    }

}
