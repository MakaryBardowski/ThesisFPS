package messages.lobby;

import client.ClientGameAppState;
import client.Main;
import client.MainMenuController;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import messages.TwoWayMessage;
import server.ServerMain;

@Serializable
public class GameStartedMessage extends TwoWayMessage {

    public GameStartedMessage() {
        setReliable(true);
    }

    public GameStartedMessage(int connectionId, int classId) {
        setReliable(true);
    }

    @Override
    public void handleServer(ServerMain serverMain,HostedConnection hc) {
    }

    @Override
    public void handleClient(ClientGameAppState clientApp) {
        Main.getInstance().enqueue(() -> {
            MainMenuController.leaveLobby();
            clientApp.joinGame();
        });
    }

}
