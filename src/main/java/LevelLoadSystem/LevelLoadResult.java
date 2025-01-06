package LevelLoadSystem;

import LevelLoadSystem.entitySpawnData.EntitySpawnData;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jme3.math.Vector3f;
import game.map.blocks.Map;

import java.util.List;

@JsonDeserialize(using = LevelLoadResultDeserializer.class)
public class LevelLoadResult {
    private final Map map;
    private final List<EntitySpawnData> savedEntityData;
    private final List<Vector3f> playerSpawnpoints;

    public LevelLoadResult(byte[] logicMap,int mapSizeX, int mapSizeY, int mapSizeZ, int blockSize, List<EntitySpawnData> savedEntityData, List<Vector3f> playerSpawnpoints){
        this.map = new Map(logicMap,mapSizeX,mapSizeY,mapSizeZ,blockSize);
        this.savedEntityData = savedEntityData;
        this.playerSpawnpoints = playerSpawnpoints;
    }

    public Map getMap(){
        return map;
    }

    public List<EntitySpawnData> getSavedEntityData(){
        return savedEntityData;
    }

    public List<Vector3f> getPlayerSpawnpoints() { return playerSpawnpoints; };
}
