package game.map;

import game.map.blocks.Map;
import game.map.proceduralGeneration.Room;
import java.util.ArrayList;
import java.util.List;
import jme3utilities.math.Vector3i;
import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public class MapGenerationResult {
    private Map map;
    private List<Room> rooms;
}
