package game.map;

import LevelLoadSystem.LevelLoader;
import LevelLoadSystem.entitySpawnData.EntitySpawnData;
import client.Main;
import game.map.proceduralGeneration.RandomMapGenerator;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ServerLevelGenerator {
    private static final String MAPS_PWD = Main.isIDEMode() ? "assets" :  Paths.get("").toAbsolutePath().toString();
    private static final String LEVEL_FILE_EXTENSION = ".json";
    private static final String BOSS_LEVEL_FILEPATH_PREFIX = "assets/Maps/bossRoom";
    private static final String INVALID_MAP_TYPE_FOR_FILE_LOADING_PROVIDED_MESSAGE = "cannot load map of given type '%s'. Maps can be loaded with types ARMORY,BOSS";

    public static final String SAVED_MAP_FILEPATH_TEMPLATE = MAPS_PWD + "/Maps/Map";


    private final long levelSeed;
    private final MapType mapType;
    private final int levelIndex;

    public ServerLevelGenerator(long levelSeed, MapType mapType, int levelIndex){
        this.levelSeed = levelSeed;
        this.mapType = mapType;
        this.levelIndex = levelIndex;
    }

    public LevelGenerationResult generateLevel(int generatedMapSizeX, int generatedMapSizeY, int generatedMapSizeZ) throws IOException {
        switch (mapType) {
            case STATIC: {
                var levelFilePath = getSavedLevelFilepath(mapType, levelIndex);
                var levelLoadResult = new LevelLoader().readLevelFile(levelFilePath);
                var map = levelLoadResult.getMap();
                var entitySpawnData  = levelLoadResult.getSavedEntityData();
                return new LevelGenerationResult(map,entitySpawnData,levelLoadResult.getPlayerSpawnpoints());
            }
            case CASUAL: {
                var mapGenResult = new RandomMapGenerator(levelSeed, generatedMapSizeX,generatedMapSizeY,generatedMapSizeZ).createRandomMap();
                var entitySpawnDataGenerator = new EntitySpawnDataGenerator(levelSeed);
                var entitySpawnData = entitySpawnDataGenerator.generateEntitySpawnData(mapGenResult);
                var playerSpawnpoints = entitySpawnDataGenerator.generatePlayerSpawnpoints(mapGenResult);
                return new LevelGenerationResult(mapGenResult.getMap(),entitySpawnData, playerSpawnpoints);
            }
            default: {
                var levelFilePath = getSavedLevelFilepath(mapType, levelIndex);
                var levelLoadResult = new LevelLoader().readLevelFile(levelFilePath);
                var map = levelLoadResult.getMap();
                var entitySpawnData  = levelLoadResult.getSavedEntityData();
                return new LevelGenerationResult(map,entitySpawnData,levelLoadResult.getPlayerSpawnpoints());
            }
        }
    }


    public String getSavedLevelFilepath(MapType mapType, int levelIndex){
        if(mapType == MapType.STATIC) {
            return SAVED_MAP_FILEPATH_TEMPLATE+levelIndex+LEVEL_FILE_EXTENSION;
        }
        throw new IllegalArgumentException(String.format(INVALID_MAP_TYPE_FOR_FILE_LOADING_PROVIDED_MESSAGE,mapType));
    }

}
