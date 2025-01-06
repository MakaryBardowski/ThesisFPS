package game.map;

import LevelLoadSystem.entitySpawnData.EntitySpawnData;
import com.jme3.math.Vector3f;
import game.map.blocks.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public class LevelGenerationResult {
    private Map logicMap;
    private List<EntitySpawnData> entitySpawnData;
    private List<Vector3f> playerSpawnpoints;
}
