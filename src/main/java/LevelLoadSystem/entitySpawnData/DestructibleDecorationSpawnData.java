package LevelLoadSystem.entitySpawnData;

import com.jme3.math.Vector3f;
import game.entities.DecorationTemplates;
import game.entities.InteractiveEntity;
import server.ServerLevelManager;

public class DestructibleDecorationSpawnData extends EntitySpawnData{
    public DestructibleDecorationSpawnData(int templateIndex, Vector3f position) {
        super(templateIndex, position);
    }

    @Override
    public InteractiveEntity serverSpawn(ServerLevelManager serverLevelManager) {
        return serverLevelManager.registerDestructibleDecoration(DecorationTemplates.getTemplateByIndex(templateIndex),this.getPosition());
    }
}
