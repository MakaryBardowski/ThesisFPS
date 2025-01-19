package messages.lobby;

import client.appStates.ClientGameAppState;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import messages.TwoWayMessage;
import server.ServerMain;

@Serializable
public class HostChangedPlayerClassMessage extends TwoWayMessage {

    private int connectionId;
    private int classId;


    public HostChangedPlayerClassMessage() {
        setReliable(true);
    }

    public HostChangedPlayerClassMessage(int connectionId, int classId) {
        this.connectionId = connectionId;
        this.classId = classId;
        setReliable(true);
    }

    @Override
    public void handleServer(ServerMain serverMain,HostedConnection hc) {
        var server = ServerMain.getInstance().getServer();
        var newHc = server.getConnection(connectionId);
        
        newHc.setAttribute("class", classId);
    }

    @Override
    public void handleClient(ClientGameAppState client) {
    }


}
