package game.map.proceduralGeneration;

import game.map.MapGenerationResult;

import java.util.Random;

public abstract class MapGenerator {
    protected final Random random;
    protected final int mapSizeX;
    protected final int mapSizeZ;
    protected final int mapSizeY;

    public MapGenerator(long seed, int mapSizeX, int mapSizeY, int mapSizeZ) {
        this.random = new Random(seed);
        this.mapSizeX = mapSizeX;
        this.mapSizeY = mapSizeY;
        this.mapSizeZ = mapSizeZ;
    }

    public abstract MapGenerationResult createRandomMap();
}
