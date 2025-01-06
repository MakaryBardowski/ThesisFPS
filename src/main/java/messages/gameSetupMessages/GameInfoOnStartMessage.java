package messages.gameSetupMessages;

import client.ClientGameAppState;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import game.map.MapType;
import java.util.Arrays;
import lombok.NoArgsConstructor;
import messages.TwoWayMessage;
import server.ServerMain;

@NoArgsConstructor
@Serializable
public class GameInfoOnStartMessage extends TwoWayMessage {

    private static String CLIENT_INVALID_GAMEMODE_MESSAGE = "There is no gamemode associated with id: ";
    private int gamemodeId;
    private long[] mapSeeds;
    private MapType[] mapTypes;

    public GameInfoOnStartMessage(int gamemodeId, long[] mapSeeds, MapType[] mapTypes) {
        this.gamemodeId = gamemodeId;
        this.mapSeeds = mapSeeds;
        this.mapTypes = mapTypes;
        setReliable(true);
    }

    @Override
    public void handleServer(ServerMain server,HostedConnection hc) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        var clientLevelManager = ClientGameAppState.getInstance().getCurrentGamemode().getLevelManager();
        clientLevelManager.setLevelSeeds(mapSeeds);
        clientLevelManager.setLevelTypes(mapTypes);
        clientLevelManager.jumpToLevel(0);
    }


}   
