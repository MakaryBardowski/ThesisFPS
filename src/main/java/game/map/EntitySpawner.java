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

    public void spawnRandomMobs(byte[][][] logicMap) {
        var server = ServerMain.getInstance();
        var serverLevelManager = server.getCurrentGamemode().getLevelManager();
        var blockSize = server.getBLOCK_SIZE();

//        for (int i = 0; i < 50; i++) {
//            Vector3f pos = new Vector3f(random.nextInt(37 * blockSize) + blockSize, blockSize, random.nextInt(37 * blockSize) + blockSize);
////            4.5
////            if(logicMap[][][] != 0){
////            }
//            serverLevelManager.registerRandomChest(pos);
//        }
//if(levelIndex == 1){
        for (int i = 0; i < 30; i++) {
            var mobPos = new Vector3f(random.nextInt(37 * blockSize) + 0.5f * blockSize, blockSize, random.nextInt(37 * blockSize) + 0.5f * blockSize);
            while (!GridUtils.isSpotEmpty(mobPos, logicMap)) {
                mobPos = new Vector3f(random.nextInt(37 * blockSize) + 0.5f * blockSize, blockSize, random.nextInt(37 * blockSize) + 0.5f * blockSize);
            }

//            var randomNumber = random.nextInt(5);
//            if (randomNumber < 4) {
//                MudBeetle mob = (MudBeetle) serverLevelManager.registerMob(MobSpawnType.MUD_BEETLE);
//                mob.addAi();
//                mob.setPositionServer(mobPos);
//            } else {
            HumanMob mob = (HumanMob) serverLevelManager.createAndRegisterMob(MobSpawnType.HUMAN);
            mob.addAi();
            mob.setPositionServer(mobPos);
        }
//        }
//}

//        for (int i = 0; i < 50; i++) {
//
//            var playerSpawnpointOffset = new Vector3f(spawnpointOffset, 0, 0);
//            if (new Random().nextBoolean() == false) {
//                playerSpawnpointOffset = new Vector3f(0, 0, spawnpointOffset);
//            }
//
//            serverLevelManager.registerRandomDestructibleDecoration(new Vector3f(random.nextInt(37 * blockSize - (int) playerSpawnpointOffset.getX()) + blockSize, blockSize, random.nextInt(37 * blockSize - (int) playerSpawnpointOffset.getZ()) + blockSize)
//                    .addLocal(playerSpawnpointOffset)
//            );
//        }
    }

}
