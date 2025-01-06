package LevelLoadSystem.entitySpawnData;

import com.jme3.math.Vector3f;
import game.entities.DecorationTemplates;
import game.entities.InteractiveEntity;
import server.ServerLevelManager;

public class IndestructibleDecorationSpawnData extends EntitySpawnData{
    public IndestructibleDecorationSpawnData(int templateIndex, Vector3f position) {
        super(templateIndex, position);
    }

    @Override
    public InteractiveEntity serverSpawn(ServerLevelManager serverLevelManager) {
        return serverLevelManager.registerIndestructibleDecoration(DecorationTemplates.getTemplateByIndex(templateIndex),this.getPosition());
    }
}
