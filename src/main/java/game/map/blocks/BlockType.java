package game.map.blocks;

import com.jme3.math.Vector2f;

public class BlockType {
    public static final float ONE_SIXTEENTH = 0.0625f;
    
    public static final BlockType STONE = new BlockType((byte) 1,"Textures/Blocks/testRoomTile.png", new Vector2f(0, 0.0f), new Vector2f(ONE_SIXTEENTH, ONE_SIXTEENTH));
    public static final BlockType DIRT_STONES = new BlockType((byte) 2,"Textures/Blocks/wallTrench.png", new Vector2f(ONE_SIXTEENTH, 0.0f), new Vector2f(ONE_SIXTEENTH*2, ONE_SIXTEENTH));
    public static final BlockType DIRT = new BlockType((byte) 3,"Textures/Blocks/wallTrench1.png", new Vector2f(ONE_SIXTEENTH*2, 0.0f), new Vector2f(ONE_SIXTEENTH*3, ONE_SIXTEENTH));
    public static final BlockType WATER = new BlockType((byte) 4,"Textures/Blocks/water.png", new Vector2f(ONE_SIXTEENTH*3, 0.0f), new Vector2f(ONE_SIXTEENTH*7, ONE_SIXTEENTH));
    public static final BlockType OFFICE_BOTTOM = new BlockType((byte) 5,"Textures/Blocks/wallOfficeBottom.png", new Vector2f(ONE_SIXTEENTH*7, 0.0f), new Vector2f(ONE_SIXTEENTH*8, ONE_SIXTEENTH));
    public static final BlockType OFFICE_TOP = new BlockType((byte) 6,"Textures/Blocks/wallOfficeTop.png", new Vector2f(ONE_SIXTEENTH*8, 0.0f), new Vector2f(ONE_SIXTEENTH*9, ONE_SIXTEENTH));
    public static final BlockType OFFICE_FLOOR = new BlockType((byte) 7,"Textures/Blocks/floorOffice.png", new Vector2f(ONE_SIXTEENTH*9, 0.0f), new Vector2f(ONE_SIXTEENTH*10, ONE_SIXTEENTH));

//        public static final BlockType WATER = new BlockType("Textures/Blocks/water.png", new Vector2f(0, 0.0f), new Vector2f(1, 1));
    public byte blockId;
    public String textureName;
    public Vector2f minTexCoord;
    public Vector2f maxTexCoord;

    public BlockType(byte blockId, String s, Vector2f min, Vector2f max) {
        this.blockId = blockId;
        textureName = s;
        minTexCoord = min;
        maxTexCoord = max;
    }

}
