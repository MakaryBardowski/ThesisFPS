package game.map.proceduralGeneration;

import game.map.MapGenerationResult;

public class CellularAutomataMapGenerator extends MapGenerator{
    public CellularAutomataMapGenerator(long seed, int mapSizeX, int mapSizeY, int mapSizeZ) {
        super(seed, mapSizeX, mapSizeY, mapSizeZ);
    }

    @Override
    public MapGenerationResult createRandomMap() {
        return null;
    }
}
