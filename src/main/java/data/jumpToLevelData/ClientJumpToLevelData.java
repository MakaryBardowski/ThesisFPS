package data.jumpToLevelData;

import com.jme3.math.Vector3f;
import game.map.MapType;
import lombok.Getter;

import java.util.Map;

@Getter
public class ClientJumpToLevelData extends BaseJumpToLevelData {
    private final Map<Integer, Vector3f> playerSpawnpointsByPlayerId;

    public ClientJumpToLevelData(int levelIndex, long levelSeed, MapType levelType, Map<Integer, Vector3f> playerSpawnpointsByPlayerId) {
        super(levelIndex, levelSeed, levelType);
        this.playerSpawnpointsByPlayerId = playerSpawnpointsByPlayerId;
    }
}
