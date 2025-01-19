package messages;

import client.appStates.ClientGameAppState;
import com.jme3.math.Vector3f;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import game.entities.Chest;
import lombok.Getter;
import server.ServerMain;

@Serializable
public class NewChestMessage extends TwoWayMessage {

    @Getter
    private int id;
    @Getter
    private float health;
    private float x;
    private float y;
    private float z;

    public NewChestMessage() {
    }

    public NewChestMessage(Chest chest, Vector3f pos) {
        this.id = chest.getId();
        this.health = chest.getHealth();
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
    }

    public Vector3f getPos() {
        return new Vector3f(x, y, z);
    }

    @Override
    public void handleServer(ServerMain server,HostedConnection hc) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        addNewChest(this);
    }

    private void addNewChest(NewChestMessage nmsg) {
        if (entityNotExistsLocallyClient(nmsg.getId())) {
            enqueueExecution(() -> {
                Chest c = Chest.createRandomChestClient(nmsg.getId(), ClientGameAppState.getInstance().getDestructibleNode(), nmsg.getPos(), ClientGameAppState.getInstance().getAssetManager());
                ClientGameAppState.getInstance().getMobs().put(c.getId(), c);
                c.setHealth(nmsg.getHealth());
                ClientGameAppState.getInstance().getGrid().insert(c);
            });
        }
    }

}
