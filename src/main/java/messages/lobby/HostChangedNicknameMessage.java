package messages.lobby;

import client.ClientGameAppState;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import messages.TwoWayMessage;
import static messages.lobby.HostJoinedLobbyMessage.updateLobby;
import server.ServerMain;

@Serializable
public class HostChangedNicknameMessage extends TwoWayMessage {

    private int connectionId;
    private String nick;


    public HostChangedNicknameMessage() {
        setReliable(true);
    }

    public HostChangedNicknameMessage(int connectionId, String nick) {
        this.connectionId = connectionId;
        this.nick = nick;
        setReliable(true);
    }

    @Override
    public void handleServer(ServerMain serverMain,HostedConnection hc) {
        var server = ServerMain.getInstance().getServer();
        var newHc = server.getConnection(connectionId);
        
        newHc.setAttribute("nick", nick);
//        System.out.println("connection"+connectionId+" new name "+newHc.getAttribute("nick"));
        server.broadcast(this);
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        updateLobby(connectionId,nick);
    }


}