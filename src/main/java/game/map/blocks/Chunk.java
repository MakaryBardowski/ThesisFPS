package game.map.blocks;

import com.jme3.math.Vector2f;
import com.jme3.scene.Geometry;
import com.jme3.texture.Texture;
import lombok.Getter;

public class Chunk {

    private final ChunkLayer staticBlockLayer;
    private final ChunkLayer waterBlockLayer;

    private Geometry geometry;

    @Getter
    private final Vector2f chunkPos;

    public Chunk(BlockWorld bw, int x, int z) {
        chunkPos = new Vector2f(x, z);
        staticBlockLayer = new ChunkLayer(bw, x, z, false);
        waterBlockLayer = new ChunkLayer(bw, x, z, true);
    }

    public final Geometry attach() {
        staticBlockLayer.generateGeometry();
        waterBlockLayer.generateGeometry();
        return geometry;
    }

    public Block attachBlock(Block b, Texture t) {
        if (b.getBlockType() == BlockType.WATER) {
            waterBlockLayer.attachBlock(b, t);
            return b;
        }
        staticBlockLayer.attachBlock(b, t);
        return b;
    }

    public Block addBlockData(Block b, Texture t) {
        if (b.getBlockType() == BlockType.WATER) {
            waterBlockLayer.addBlockData(b, t);
            return b;
        }

        staticBlockLayer.addBlockData(b, t);
        return b;
    }

    public Block detachBlock(Block b) {
        if (b.getBlockType() == BlockType.WATER) {
            waterBlockLayer.detachBlock(b);
            return b;
        }

        staticBlockLayer.detachBlock(b);
        return b;
    }

    public void clear() {
        staticBlockLayer.clear();
        waterBlockLayer.clear();
    }

    public ChunkLayer getLayerByBlock(BlockType bt) {
        if (bt == BlockType.WATER) {
            return waterBlockLayer;
        }
        return staticBlockLayer;
    }

}
