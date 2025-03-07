package messages;

import client.appStates.ClientGameAppState;
import com.jme3.math.Vector3f;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import game.entities.grenades.ThrownGrenade;
import lombok.Getter;
import lombok.Setter;
import server.ServerGameAppState;

@Serializable
public class ThrownGrenadeExplodedMessage extends TwoWayMessage {

    @Getter
    @Setter
    private int id;
    private float posX;
    private float posY;
    private float posZ;

    public ThrownGrenadeExplodedMessage() {
    }

    // if sent  client -> server it contains the data about the grenade item the thrown grenade originates from
    // if sent  server -> client it contains the data about the new thrown grenade and its position
    public ThrownGrenadeExplodedMessage(int id, Vector3f pos) {
        this.id = id;
        this.posX = pos.getX();
        this.posY = pos.getY();
        this.posZ = pos.getZ();
    }

    public Vector3f getPos() {
        return new Vector3f(posX, posY, posZ);
    }

    @Override
    public void handleServer(ServerGameAppState server, HostedConnection hc) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        handleGrenadeExplosion(this);
    }

    private void handleGrenadeExplosion(ThrownGrenadeExplodedMessage gemsg) {
        enqueueExecution(() -> {
            ThrownGrenade g = (ThrownGrenade) getEntityByIdClient(gemsg.getId());
                g.getNode().setLocalTranslation(gemsg.getPos());
                g.explodeClient();
                ClientGameAppState.getInstance().getMobs().remove(gemsg.getId());
                g.getNode().removeFromParent();
            
        });
    }

}
