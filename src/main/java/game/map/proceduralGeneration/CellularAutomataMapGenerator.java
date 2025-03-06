package game.map.proceduralGeneration;

import client.appStates.ClientGameAppState;
import game.map.MapGenerationResult;
import game.map.blocks.BlockType;
import game.map.blocks.Map;


public class CellularAutomataMapGenerator extends MapGenerator{
    public CellularAutomataMapGenerator(long seed, int mapSizeX, int mapSizeY, int mapSizeZ) {
        super(seed, mapSizeX, mapSizeY, mapSizeZ);
    }

    public MapGenerationResult createRandomMap() {
        byte[] flatMap = new byte[mapSizeX*mapSizeY*mapSizeZ];
        Map logicMap = new Map(flatMap,mapSizeX,mapSizeY,mapSizeZ, ClientGameAppState.getInstance().getBLOCK_SIZE());

        var airToWallRatio = 0.7f;
        for(int x = 0; x < mapSizeX; x++) {
            for(int z = 0; z < mapSizeY; z++) {
                var blockId =  random.nextFloat() <= airToWallRatio ? (byte) 0 : BlockType.STONE.blockId;
                logicMap.setBlockIdAtPosition(x,1,z,blockId);
                logicMap.setBlockIdAtPosition(x,2,z,blockId);
                logicMap.setBlockIdAtPosition(x,3,z,blockId);

                logicMap.setBlockIdAtPosition(x,0,z,BlockType.DIRT.blockId);

            }
        }


        var maxUpdates = 6;

        for(int update = 0; update < maxUpdates; update++) {
            for(int x = 0; x < mapSizeX; x++) {
                for(int z = 0; z < mapSizeY; z++) {
                    var neighbors = 0;
                    for(int dx =-1 ; dx <= 1 ; dx++) {
                        for(int dz =-1 ; dz <= 1 ; dz++) {
                            if(dz != 0 && dx != 0 && logicMap.isWithinMapBounds(x+dx,1,z+dz) && logicMap.getBlockIdAtPosition(x+dx,1,z+dz) != 0){
                                neighbors++;
                            }
                        }
                    }

                    if(neighbors == 5){
                        logicMap.setBlockIdAtPosition(x,1,z, BlockType.STONE.blockId);
                        logicMap.setBlockIdAtPosition(x,2,z,BlockType.STONE.blockId);
                        logicMap.setBlockIdAtPosition(x,3,z,BlockType.STONE.blockId);

                    }

                }
            }
        }


        var mapGenResult = new MapGenerationResult(logicMap,null);
        return mapGenResult;
    }
}
