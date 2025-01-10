package LevelLoadSystem.entitySpawnData;

import com.jme3.math.Vector3f;
import game.entities.Entity;
import game.entities.factories.MobSpawnType;
import server.ServerLevelManager;

public class MobSpawnData extends EntitySpawnData{
    public MobSpawnData(int templateIndex, Vector3f position) {
        super(templateIndex, position);
    }

    public MobSpawnData(MobSpawnType template, Vector3f position) {
        super(template.ordinal(), position);
    }

    @Override
    public Entity serverSpawn(ServerLevelManager serverLevelManager) {
        var mob = serverLevelManager.createAndRegisterMob(MobSpawnType.values()[templateIndex]);
        mob.setPositionServer(getPosition());
        return mob;
    }
}
