package messages;

import client.appStates.ClientGameAppState;
import com.jme3.math.Vector3f;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import game.entities.mobs.player.Player;
import lombok.Getter;
import server.ServerGameAppState;

@Serializable
public class PlayerJoinedMessage extends TwoWayMessage {

    @Getter
    private int id;
    private float x;
    private float y;
    private float z;
    @Getter
    private String name;
    private int classIndex;

    public PlayerJoinedMessage() {
    }

    public PlayerJoinedMessage(int id, Vector3f pos, String name, int classIndex) {
        this.id = id;
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.name = name;
        this.classIndex = classIndex;
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
        addNewPlayer(this);
    }

    private void addNewPlayer(PlayerJoinedMessage nmsg) {
        if (entityNotExistsLocallyClient(nmsg.getId())) {
            enqueueExecution(
                    () -> {
                        createOtherPlayer(nmsg);
                    }
            );
        }
    }

    private void createOtherPlayer(PlayerJoinedMessage nmsg) {
        Player p = registerOtherPlayer(nmsg);
        placeOtherPlayer(nmsg, p);
        p.setName(nmsg.getName());
    }

    private void placeOtherPlayer(PlayerJoinedMessage nmsg, Player p) {
        placeMob(nmsg.getPos(), p);
    }

    private Player registerOtherPlayer(PlayerJoinedMessage nmsg) {
        return ClientGameAppState.getInstance().registerPlayer(nmsg.getId(), false, classIndex);
    }

}
