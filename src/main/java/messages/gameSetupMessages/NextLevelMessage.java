package messages.gameSetupMessages;

import client.appStates.ClientGameAppState;
import com.jme3.math.Vector3f;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import data.jumpToLevelData.BaseJumpToLevelData;
import data.jumpToLevelData.ClientJumpToLevelData;
import game.map.MapType;
import messages.TwoWayMessage;
import server.ServerMain;

import java.util.Map;

@Serializable
public class NextLevelMessage extends TwoWayMessage {
    private int currentLevelIndex;
    private int nextLevelIndex;

    // only populated on the way back to client
    private long nextLevelSeed;
    private int nextMapTypeOrdinal;
    private Map<Integer,Vector3f> playerPositions;

    public NextLevelMessage() {
        setReliable(true);
    }

    public NextLevelMessage(int currentLevelIndex) {
        this();
        this.currentLevelIndex = currentLevelIndex;
    }


    public NextLevelMessage(int currentLevelIndex, long levelSeed, MapType mapType, Map<Integer,Vector3f> playerPositions) {
        this(currentLevelIndex);
        this.nextLevelSeed = levelSeed;
        this.nextMapTypeOrdinal = mapType.ordinal();
        this.playerPositions = playerPositions;
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
        nextLevelSeed = levelManager.getLevelSeeds()[nextLevelIndex];

        nextMapTypeOrdinal = levelManager.getLevelTypes()[nextLevelIndex].ordinal();
        levelManager.jumpToLevel(new BaseJumpToLevelData(nextLevelIndex,nextLevelSeed,getMapType()));
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        var clientLevelManager = ClientGameAppState.getInstance().getCurrentGamemode().getLevelManager();
        clientLevelManager.jumpToLevel(new ClientJumpToLevelData(currentLevelIndex, nextLevelSeed,getMapType(),playerPositions));
    }

    private MapType getMapType(){
        return MapType.values()[nextMapTypeOrdinal];
    }

}
