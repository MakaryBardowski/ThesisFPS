package game.map;

import LevelLoadSystem.entitySpawnData.EntitySpawnData;
import LevelLoadSystem.entitySpawnData.IndestructibleDecorationSpawnData;
import LevelLoadSystem.entitySpawnData.ItemSpawnData;
import LevelLoadSystem.entitySpawnData.MobSpawnData;
import com.jme3.math.Vector3f;
import game.entities.factories.MobSpawnType;
import game.items.ItemTemplates;
import game.map.proceduralGeneration.Room;
import generators.PercentageRandomGenerator;
import server.ServerMain;

import java.util.*;

public class EntitySpawnDataGenerator {
    private final long seed;
    private final int playerCount = ServerMain.MAX_PLAYERS;
    private static final PercentageRandomGenerator<MobSpawnType> mobSpawnTypeRandomGenerator;
    private static final PercentageRandomGenerator<ItemTemplates.ItemTemplate> itemTemplateRandomGenerator;

    private static final Map<Float, MobSpawnType> mobsByChance = new HashMap<>();
    private static final Map<Float, ItemTemplates.ItemTemplate> itemsByChance = new HashMap<>();
    static {
        mobsByChance.put(40f, MobSpawnType.HUMAN);
        mobsByChance.put(60f, MobSpawnType.MUD_BEETLE);;

        itemsByChance.put(9f, ItemTemplates.PISTOL_AMMO_PACK);
        itemsByChance.put(5f, ItemTemplates.MEDPACK);
        itemsByChance.put(8f, ItemTemplates.RIFLE_AMMO_PACK);
        itemsByChance.put(7f, ItemTemplates.PISTOL_C96);
        itemsByChance.put(1f,ItemTemplates.LMG_HOTCHKISS);

        itemsByChance.put(3f,ItemTemplates.LMG_AMMO_PACK);
        itemsByChance.put(0.5f,ItemTemplates.GAS_MASK);
        itemsByChance.put(66.5f, ItemTemplates.KNIFE);



        mobSpawnTypeRandomGenerator = new PercentageRandomGenerator<>(mobsByChance);
        itemTemplateRandomGenerator = new PercentageRandomGenerator<>(itemsByChance);
    }

    public EntitySpawnDataGenerator(long seed){
        this.seed = seed;
    }

    public List<EntitySpawnData> generateEntitySpawnData(MapGenerationResult mapGenerationResult){
        var random = new Random(seed);
        var entitySpawnData = new ArrayList<EntitySpawnData>();
        var rooms = mapGenerationResult.getRooms();
        var startingRoom = getStartingRoom(rooms);

        var minItemsPerRoom = 1;
        var maxItemsPerRoom = 3;

        var minMobsPerRoom = 2;
        var maxMobsPerRoom = 4;
        var exitNotCreated = true;

        for(int roomIndex = 0; roomIndex < rooms.size(); roomIndex++){
            var room = rooms.get(roomIndex);

            if(room.equals(startingRoom)){
                continue;
            }
            if(exitNotCreated){
                var exitSpawnData = new IndestructibleDecorationSpawnData(3,
                        new Vector3f(
                                random.nextInt(room.getStartX()+1,room.getEndX()-1)+0.5f,
                                room.getStartY()+1,
                                random.nextInt(room.getStartZ()+1,room.getEndZ()-1)+0.5f
                        ));
                entitySpawnData.add(exitSpawnData);
                exitNotCreated=false;
            }

            var actualAmountOfMobsInRoom = random.nextInt(minMobsPerRoom,maxMobsPerRoom+1);
            for(int i = 0; i < actualAmountOfMobsInRoom; i++) {
                var mobSpawnData = new MobSpawnData(mobSpawnTypeRandomGenerator.getRandom(), new Vector3f(
                        random.nextInt(room.getStartX() + 1, room.getEndX() - 1) + 0.5f,
                        room.getStartY() + 1,
                        random.nextInt(room.getStartZ() + 1, room.getEndZ() - 1) + 0.5f
                ));
                entitySpawnData.add(mobSpawnData);
            }


            var actualAmountOfItems = random.nextInt(minItemsPerRoom,maxItemsPerRoom+1);
            for(int i = 0; i < actualAmountOfItems; i++) {
                var mobSpawnData = new ItemSpawnData(itemTemplateRandomGenerator.getRandom().getTemplateIndex(), new Vector3f(
                        random.nextInt(room.getStartX() + 1, room.getEndX() - 1) + 0.5f,
                        room.getStartY() + 1,
                        random.nextInt(room.getStartZ() + 1, room.getEndZ() - 1) + 0.5f
                ),true, true);
                entitySpawnData.add(mobSpawnData);
            }

        }



        return entitySpawnData;
    }

    public List<Vector3f> generatePlayerSpawnpoints(MapGenerationResult mapGenerationResult){
        List<Vector3f> playerSpawnpoints = new ArrayList<>();
        var random = new Random(seed);
        var startingRoom = getStartingRoom(mapGenerationResult.getRooms());


        while (playerSpawnpoints.size() < playerCount){
            var playerSpawnpoint = new Vector3f(
                    random.nextInt(startingRoom.getStartX()+1, startingRoom.getEndX()-1)+0.5f,
                    startingRoom.getStartY()+1,
                    random.nextInt(startingRoom.getStartZ()+1, startingRoom.getEndZ()-1)+0.5f
            );
            if(!playerSpawnpoints.contains(playerSpawnpoint)){
                playerSpawnpoints.add(playerSpawnpoint);
            }
        }
        System.out.println("generated spawnpoints "+playerSpawnpoints);
        return playerSpawnpoints;
    }

    public Room getStartingRoom(List<Room> rooms){
        var random = new Random(seed);
        var startingRoomIndex = random.nextInt(rooms.size());
        return rooms.get(startingRoomIndex);
    }
}
