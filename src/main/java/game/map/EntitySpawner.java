package game.map;

import LevelLoadSystem.entitySpawnData.EntitySpawnData;
import Utils.GridUtils;
import com.jme3.math.Vector3f;
import game.entities.factories.MobSpawnType;
import game.entities.mobs.HumanMob;

import java.util.List;
import java.util.Random;

import server.ServerMain;

public class EntitySpawner {

    private final Random random;
    private final int blockSize;

    public EntitySpawner(long seed, int blockSize) {
        random = new Random(seed);
        this.blockSize = blockSize;
    }


    public void spawnNewLevelEntities(List<EntitySpawnData> entitySpawnData){
        for(var entitySpawnInfo : entitySpawnData){
            var server = ServerMain.getInstance();
            var serverLevelManager = server.getCurrentGamemode().getLevelManager();
            entitySpawnInfo.getPosition().multLocal(blockSize);
            serverLevelManager.broadcastEntityOnNextLevel(entitySpawnInfo.serverSpawn(serverLevelManager));
        }
    }

}
