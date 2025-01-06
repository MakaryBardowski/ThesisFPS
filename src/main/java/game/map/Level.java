package game.map;

import game.map.blocks.BlockWorld;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import game.map.blocks.Map;

public class Level {

    private final BlockWorld blockWorld;

    public Level(int blockSize, int chunkSize, int mapSizeX, int mapSizeY, int mapSizeZ, Map logicMap, AssetManager a, Node mapNode) {
        blockWorld = new BlockWorld(blockSize, chunkSize, mapSizeX,mapSizeY,mapSizeZ, logicMap, a, mapNode);
    }

    public BlockWorld getBlockWorld() {
        return blockWorld;
    }

    public Level updateAfterLogicMapChange() {
        blockWorld.updateAfterLogicMapChange();
        return this;
    }

}
