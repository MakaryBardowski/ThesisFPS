package messages;

import client.appStates.ClientGameAppState;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import server.ServerGameAppState;

@Serializable
public class SystemHealthUpdateMessage extends TwoWayMessage {

    protected int id;
    protected float health;

    public SystemHealthUpdateMessage() {
    }

    public SystemHealthUpdateMessage(int id, float health) {
        this.id = id;
        this.health = health;
    }

    public int getId() {
        return id;
    }

    public float getHealth() {
        return health;
    }

    @Override
    public void handleServer(ServerGameAppState server, HostedConnection hc) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        updateEntityHealth(this);
    }

    private void updateEntityHealth(SystemHealthUpdateMessage hmsg) {
        if (entityExistsLocallyClient(hmsg.getId())) {
            getDestructibleByIdClient(hmsg.getId()).setHealth(hmsg.getHealth());
        }
    }

}
