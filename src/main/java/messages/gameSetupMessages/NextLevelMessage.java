package messages.gameSetupMessages;

import client.ClientGameAppState;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import messages.TwoWayMessage;
import server.ServerMain;

@Serializable
public class NextLevelMessage extends TwoWayMessage {
    private int currentLevelIndex;
    private int nextLevelIndex;

    public NextLevelMessage() {
        setReliable(true);
    }

    public NextLevelMessage(int currentLevelIndex) {
        this.currentLevelIndex = currentLevelIndex;
        setReliable(true);
    }

    @Override
    public void handleServer(ServerMain server,HostedConnection hc) {
        var levelManager = server.getCurrentGamemode().getLevelManager();
        var currentLevelIndexOnServer = levelManager.getCurrentLevelIndex();
        // ignore if old message arrived (lagged user)
        if(currentLevelIndex != currentLevelIndexOnServer){
            return;
        }
        nextLevelIndex = levelManager.getCurrentLevelIndex()+1;
        levelManager.jumpToLevel(nextLevelIndex);
        server.getServer().broadcast(this);
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        var clientLevelManager = ClientGameAppState.getInstance().getCurrentGamemode().getLevelManager();
        clientLevelManager.jumpToLevel(nextLevelIndex);
    }


}
