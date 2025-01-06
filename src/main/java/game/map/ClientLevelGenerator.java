package game.map;

import LevelLoadSystem.LevelLoader;
import LevelLoadSystem.entitySpawnData.EntitySpawnData;
import client.ClientGameAppState;
import client.Main;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import game.map.blocks.Map;
import game.map.proceduralGeneration.RandomMapGenerator;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ClientLevelGenerator {
    private final long levelSeed;
    private final MapType mapType;
    private final int blockSize;
    private final int chunkSize;
    private final Node mapNode;
    private final AssetManager assetManager;

    public ClientLevelGenerator(long levelSeed, MapType mapType, int blockSize, int chunkSize, Node mapNode){
        this.levelSeed = levelSeed;
        this.mapType = mapType;
        this.blockSize = blockSize;
        this.chunkSize = chunkSize;
        this.mapNode = mapNode;
        this.assetManager = ClientGameAppState.getInstance().getAssetManager();
    }

    public Level generateLevel(int generatedMapSizeX, int generatedMapSizeY, int generatedMapSizeZ) throws IOException {
        switch (mapType) {
            case STATIC: {
                return null;
            }
            case CASUAL: {
                var mapGenResult =  new RandomMapGenerator(levelSeed, generatedMapSizeX,generatedMapSizeY,generatedMapSizeZ).createRandomMap();
                return new Level(blockSize, chunkSize, generatedMapSizeX,generatedMapSizeY,generatedMapSizeZ, mapGenResult.getMap(), assetManager, mapNode);
            }
            default: {
                return null;
            }
        }
    }

    public Level generateFromMap(Map logicMap){
        return new Level(logicMap.getBlockSize(),chunkSize,logicMap.getMapSizeX(), logicMap.getMapSizeY(), logicMap.getMapSizeZ(), logicMap, assetManager, mapNode);
    }
}
