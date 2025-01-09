package LevelLoadSystem.entitySpawnData;

import com.jme3.math.Vector3f;
import game.entities.DecorationTemplates;
import game.entities.Entity;
import server.ServerLevelManager;

public class DestructibleDecorationSpawnData extends EntitySpawnData{
    public DestructibleDecorationSpawnData(int templateIndex, Vector3f position) {
        super(templateIndex, position);
    }

    @Override
    public Entity serverSpawn(ServerLevelManager serverLevelManager) {
        return serverLevelManager.createAndRegisterDestructibleDecoration(DecorationTemplates.getTemplateByIndex(templateIndex),this.getPosition());
    }
}
