package messages;

import client.appStates.ClientGameAppState;
import com.jme3.math.Vector3f;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import game.entities.grenades.ThrownGrenade;
import server.ServerMain;

@Serializable
public class GrenadePosUpdateMessage extends EntityUpdateMessage {

    protected float x;
    protected float y;
    protected float z;
    
    public GrenadePosUpdateMessage() {
    }

    public GrenadePosUpdateMessage(int id, Vector3f pos) {
        super(id);
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
    }

    public Vector3f getPos(){
    return new Vector3f(x,y,z);
    }
    

    @Override
    public String toString() {
        return "MobUpdatePosRotMessage{ id="+id + " x=" + x + ", y=" + y + ", z=" + z +'}';
    }

    @Override
    public void handleServer(ServerMain server,HostedConnection hc) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        if (getEntityByIdClient(id) != null) {
            ((ThrownGrenade) getEntityByIdClient(id)).setServerLocation(getPos());
        }
    }

}
