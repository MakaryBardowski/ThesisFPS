package game.map;

import LevelLoadSystem.entitySpawnData.EntitySpawnData;

import java.util.List;
import java.util.Random;

import server.ServerGameAppState;

public class EntitySpawner {

    private final Random random;
    private final int blockSize;

    public EntitySpawner(long seed, int blockSize) {
        random = new Random(seed);
        this.blockSize = blockSize;
    }


    public void spawnNewLevelEntities(List<EntitySpawnData> entitySpawnData){
        for(var entitySpawnInfo : entitySpawnData){
            var server = ServerGameAppState.getInstance();
            var serverLevelManager = server.getCurrentGamemode().getLevelManager();
            entitySpawnInfo.getPosition().multLocal(blockSize);
            serverLevelManager.broadcastEntityOnNextLevel(entitySpawnInfo.serverSpawn(serverLevelManager));
        }
    }

}
