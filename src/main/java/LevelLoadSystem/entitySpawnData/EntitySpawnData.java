package LevelLoadSystem.entitySpawnData;

import com.jme3.math.Vector3f;
import game.entities.InteractiveEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import server.ServerLevelManager;

@Getter
@ToString
@AllArgsConstructor
public abstract class EntitySpawnData {
    protected int templateIndex;
    protected Vector3f position;

    public abstract InteractiveEntity serverSpawn(ServerLevelManager serverLevelManager);

}
