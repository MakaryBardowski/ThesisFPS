package game.map;

import LevelLoadSystem.entitySpawnData.EntitySpawnData;
import LevelLoadSystem.entitySpawnData.IndestructibleDecorationSpawnData;
import LevelLoadSystem.entitySpawnData.ItemSpawnData;
import LevelLoadSystem.entitySpawnData.MobSpawnData;
import Utils.GridUtils;
import com.jme3.math.Vector3f;
import data.ChanceEntry;
import game.entities.factories.MobSpawnType;
import game.items.ItemTemplates;
import game.map.proceduralGeneration.Room;
import generators.PercentageRandomGenerator;
import jme3utilities.math.Vector3i;
import server.ServerGameAppState;
import server.ServerLevelManager;
import server.ServerStoryGameManager;

import java.util.*;

public class EntitySpawnDataGenerator {
    private final long seed;
    private final int playerCount = ServerGameAppState.MAX_PLAYERS;
    private static final PercentageRandomGenerator<MobSpawnType> mobSpawnTypeRandomGenerator;
    private static final PercentageRandomGenerator<ItemTemplates.ItemTemplate> itemTemplateRandomGenerator;

    private static final List<ChanceEntry<MobSpawnType>> mobsByChance = new ArrayList<>();
    private static final List<ChanceEntry<ItemTemplates.ItemTemplate>> itemsByChance = new ArrayList<>();
    static {
        mobsByChance.add(new ChanceEntry<>(40f, MobSpawnType.SOLDIER));
        mobsByChance.add(new ChanceEntry<>(60f, MobSpawnType.MUD_BEETLE));


        itemsByChance.add(new ChanceEntry<>(8f, ItemTemplates.PISTOL_AMMO_PACK));
        itemsByChance.add(new ChanceEntry<>(5f, ItemTemplates.MEDPACK));
        itemsByChance.add(new ChanceEntry<>(7f, ItemTemplates.RIFLE_AMMO_PACK));
        itemsByChance.add(new ChanceEntry<>(5f, ItemTemplates.PISTOL_C96));
        itemsByChance.add(new ChanceEntry<>(1.25f, ItemTemplates.RIFLE_BORYSOV));
        itemsByChance.add(new ChanceEntry<>(1.5f, ItemTemplates.RIFLE_MANNLICHER_95));
        itemsByChance.add(new ChanceEntry<>(0.75f,ItemTemplates.LMG_HOTCHKISS));

        itemsByChance.add(new ChanceEntry<>(2.5f,ItemTemplates.LMG_AMMO_PACK));

        itemsByChance.add(new ChanceEntry<>(3f,ItemTemplates.GAS_MASK));
        itemsByChance.add(new ChanceEntry<>(5f, ItemTemplates.VEST_TRENCH));
        itemsByChance.add(new ChanceEntry<>(5f, ItemTemplates.BOOTS_TRENCH));
        itemsByChance.add(new ChanceEntry<>(3f, ItemTemplates.TRENCH_HELMET));

        itemsByChance.add(new ChanceEntry<>(39.25f, ItemTemplates.KNIFE));
        itemsByChance.add(new ChanceEntry<>(13.75f, ItemTemplates.AXE));


        mobSpawnTypeRandomGenerator = new PercentageRandomGenerator<>(mobsByChance);
        itemTemplateRandomGenerator = new PercentageRandomGenerator<>(itemsByChance);
    }

    public EntitySpawnDataGenerator(long seed){
        this.seed = seed;
    }

    public List<EntitySpawnData> generateEntitySpawnData(MapGenerationResult mapGenerationResult){
        var random = new Random(seed>>5);
        var entitySpawnData = new ArrayList<EntitySpawnData>();
        var minItemsPerRoom = 0;
        var maxItemsPerRoom = 3;

        var minMobsPerRoom = 1;
        var maxMobsPerRoom = 4;
        var exitNotCreated = true;


        if(mapGenerationResult.getRooms()==null){

            while(exitNotCreated) {
                int x = random.nextInt(mapGenerationResult.getMap().getMapSizeX());
                int z = random.nextInt(mapGenerationResult.getMap().getMapSizeZ());
                if(mapGenerationResult.getMap().getBlockIdAtPosition(x,1,z)!=0){
                    continue;
                }
                    var exitSpawnData = new IndestructibleDecorationSpawnData(3,
                            new Vector3f(
                                    x + 0.5f,
                                    1,
                                    z+ 0.5f
                            ));
                    entitySpawnData.add(exitSpawnData);
                    exitNotCreated = false;
            }

            var minDistance = 20f/3;
            for(int i = 0; i < 16; i++) {
                int mobX = random.nextInt(mapGenerationResult.getMap().getMapSizeX());
                int mobZ = random.nextInt(mapGenerationResult.getMap().getMapSizeZ());
                var spawnpoints = generatePlayerSpawnpoints(mapGenerationResult);
                while(true){
                     mobX = random.nextInt(mapGenerationResult.getMap().getMapSizeX());
                     mobZ = random.nextInt(mapGenerationResult.getMap().getMapSizeZ());
                    var mobPos = new Vector3f(mobX, 1, mobZ);
                    boolean canPlace = true;
                    for(var spawnpoint : spawnpoints){
                        if(spawnpoint.distance(mobPos) < minDistance){
                            canPlace = false;
                        }
                    }
                    if(canPlace){
                        var mobSpawnData = new MobSpawnData(mobSpawnTypeRandomGenerator.getRandom(), new Vector3f(
                                mobX + 0.5f,
                                1,
                                mobZ+ 0.5f
                        ));
                        entitySpawnData.add(mobSpawnData);
                        break;
                    }
                }
            }

            return entitySpawnData;
        }

        var rooms = mapGenerationResult.getRooms();
        var startingRoom = getStartingRoom(rooms);

        for(int roomIndex = 0; roomIndex < rooms.size(); roomIndex++){
            var room = rooms.get(roomIndex);

            if(room.equals(startingRoom)){
                continue;
            }
            if(exitNotCreated){
                var exitSpawnData = new IndestructibleDecorationSpawnData(3,
                        new Vector3f(
                                random.nextInt(room.getStartX(),room.getEndX())+0.5f,
                                room.getStartY()+1,
                                random.nextInt(room.getStartZ(),room.getEndZ())+0.5f
                        ));
                entitySpawnData.add(exitSpawnData);
                exitNotCreated=false;
            }

            var actualAmountOfMobsInRoom = random.nextInt(minMobsPerRoom,maxMobsPerRoom+1);
            for(int i = 0; i < actualAmountOfMobsInRoom; i++) {
                var mobSpawnData = new MobSpawnData(mobSpawnTypeRandomGenerator.getRandom(), new Vector3f(
                        random.nextInt(room.getStartX(), room.getEndX()) + 0.5f,
                        room.getStartY() + 1,
                        random.nextInt(room.getStartZ(), room.getEndZ()) + 0.5f
                ));
                if(ServerGameAppState.getInstance() != null && ServerGameAppState.getInstance().getCurrentGamemode().getLevelManager().getCurrentLevelIndex() == ServerStoryGameManager.LEVEL_COUNT-1){
                    entitySpawnData.add(new MobSpawnData(MobSpawnType.RED_HAND_1, new Vector3f(
                            random.nextInt(room.getStartX(), room.getEndX()) + 0.5f,
                            room.getStartY() + 1,
                            random.nextInt(room.getStartZ(), room.getEndZ()) + 0.5f
                    )));

                }
                entitySpawnData.add(mobSpawnData);
            }


            var actualAmountOfItems = random.nextInt(minItemsPerRoom,maxItemsPerRoom+1);
            for(int i = 0; i < actualAmountOfItems; i++) {
                var mobSpawnData = new ItemSpawnData(itemTemplateRandomGenerator.getRandom().getTemplateIndex(), new Vector3f(
                        random.nextInt(room.getStartX(), room.getEndX()) + 0.5f,
                        room.getStartY() + 1,
                        random.nextInt(room.getStartZ(), room.getEndZ()) + 0.5f
                ),true, true);
                entitySpawnData.add(mobSpawnData);
            }

        }



        return entitySpawnData;
    }

    public List<Vector3f> generatePlayerSpawnpoints(MapGenerationResult mapGenerationResult){
        List<Vector3f> playerSpawnpoints = new ArrayList<>();
        var random = new Random(seed);


        if(mapGenerationResult.getRooms() == null){
            Vector3i randomCell;
            while(true){
                int x = random.nextInt(0,mapGenerationResult.getMap().getMapSizeX());
                int y = 1;
                int z = random.nextInt(0,mapGenerationResult.getMap().getMapSizeZ());
                if(mapGenerationResult.getMap().getBlockIdAtPosition(x,y,z) == 0){
                    randomCell = new Vector3i(x,y,z);
                    break;
                }
            }
            while (playerSpawnpoints.size() < playerCount) {
                var inCellOffsetX = random.nextFloat() <0.5f ? 0.2f : 0.8f;
                var inCellOffsetZ = random.nextFloat() <0.5f ? 0.2f : 0.8f;
                var playerSpawnpoint = new Vector3f(
                        randomCell.x() + inCellOffsetX,
                        1,
                        randomCell.z() + inCellOffsetZ
                );
                if (!playerSpawnpoints.contains(playerSpawnpoint)) {
                    playerSpawnpoints.add(playerSpawnpoint);
                }
            }

            return playerSpawnpoints;
        }





        var startingRoom = getStartingRoom(mapGenerationResult.getRooms());


        while (playerSpawnpoints.size() < playerCount){
            var playerSpawnpoint = new Vector3f(
                    random.nextInt(startingRoom.getStartX(), startingRoom.getEndX())+0.5f,
                    startingRoom.getStartY()+1,
                    random.nextInt(startingRoom.getStartZ(), startingRoom.getEndZ())+0.5f
            );
            if(!playerSpawnpoints.contains(playerSpawnpoint)){
                playerSpawnpoints.add(playerSpawnpoint);
            }
        }
        return playerSpawnpoints;
    }

    public Room getStartingRoom(List<Room> rooms){
        var random = new Random(seed);
        var startingRoomIndex = random.nextInt(rooms.size());
        return rooms.get(startingRoomIndex);
    }
}
