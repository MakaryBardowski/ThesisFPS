package messages;

import client.appStates.ClientGameAppState;
import com.jme3.math.Vector3f;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import game.entities.Entity;
import server.ServerMain;

@Serializable
public class InstantEntityPosCorrectionMessage extends EntityUpdateMessage {

    protected float x;
    protected float y;
    protected float z;
    
    public InstantEntityPosCorrectionMessage() {
        this.setReliable(true);
    }

    public InstantEntityPosCorrectionMessage(Entity e, Vector3f pos) {
        super(e.getId());
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.setReliable(true);
    }

    public Vector3f getPos(){
    return new Vector3f(x,y,z);
    }

    @Override
    public void handleServer(ServerMain server,HostedConnection hc) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        enqueueExecution(() -> {
            getMobByIdClient(id).setPosition(getPos());
        });
    }
    



}
