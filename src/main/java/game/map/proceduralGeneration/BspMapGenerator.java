package game.map.proceduralGeneration;

import Utils.Pair;
import client.appStates.ClientGameAppState;
import game.map.MapGenerationResult;
import game.map.blocks.Map;
import jme3utilities.math.Vector3i;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class BspMapGenerator extends MapGenerator{

    public BspMapGenerator(long seed, int mapSizeX, int mapSizeY, int mapSizeZ) {
        super(seed,mapSizeX,mapSizeY,mapSizeZ);
    }

    public MapGenerationResult createRandomMap() {
        byte[] flatMap = new byte[mapSizeX*mapSizeY*mapSizeZ];
        Arrays.fill(flatMap,(byte) 1);
        Map logicMap = new Map(flatMap,mapSizeX,mapSizeY,mapSizeZ, ClientGameAppState.getInstance().getBLOCK_SIZE());

        var initialRoom = new Room(0,0,0,mapSizeX,6,mapSizeZ);
        var maxSubdivisions = 5;
        var bspTree = partitionRoom(initialRoom, 0,maxSubdivisions);

        List<Room> rooms = new ArrayList<>();
        getRooms(rooms,bspTree);

        placeRooms(logicMap,rooms);
        connectRooms(logicMap,rooms);

        var mapGenResult = new MapGenerationResult(logicMap,rooms);
        return mapGenResult;
    }

    private void connectRooms(Map logicMap, List<Room> rooms) {
        for(int i = 0; i < rooms.size()-1; i++){
            var currentRoom = rooms.get(i);
            var nextRoom = rooms.get(i+1);

            var currentRoomConnectionPoint = getRoomConnectionPoint(currentRoom);
            var nextRoomConnectionPoint = getRoomConnectionPoint(nextRoom);

            int startX = currentRoomConnectionPoint.x();
            int endX = nextRoomConnectionPoint.x();

            int startZ = currentRoomConnectionPoint.z();
            int endZ = nextRoomConnectionPoint.z();

            if (startX <= endX) {
                for (int x = startX; x <= endX; x++) {
                    logicMap.setBlockIdAtPosition(x,0,startZ,(byte) 2);
                    logicMap.setBlockIdAtPosition(x,1,startZ,(byte) 0);
                }
            } else if (startX > endX) {
                for (int x = endX; x <= startX; x++) {
                    logicMap.setBlockIdAtPosition(x,0,startZ,(byte) 2);
                    logicMap.setBlockIdAtPosition(x,1,startZ,(byte) 0);
                }
            }

            if (startZ <= endZ) {
                for (int z = startZ; z <= endZ; z++) {
                    logicMap.setBlockIdAtPosition(endX,0,z,(byte)2);
                    logicMap.setBlockIdAtPosition(endX,1,z,(byte)0);
                }
            } else if (endZ <= startZ) {
                for (int z = endZ; z <= startZ; z++) {
                    logicMap.setBlockIdAtPosition(endX,0,z,(byte)2);
                    logicMap.setBlockIdAtPosition(endX,1,z,(byte)0);
                }
            }
        }
    }

    private void placeRooms(Map logicMap, List<Room> rooms) {
        for(final var room : rooms) {
            for (int x = room.getStartX(); x < room.getEndX(); x++) {
                for (int y = room.getStartY(); y < room.getEndY(); y++) {
                    for (int z = room.getStartZ(); z < room.getEndZ(); z++) {
                        logicMap.setBlockIdAtPosition(x, y, z, (byte) 0);
                        // hardcode dla podlogi
                        if (y == 0) {
                            logicMap.setBlockIdAtPosition(x, y, z, (byte) 2);
                        }
                        // hardcode dla podlogi

                    }
                }
            }
        }
    }

    private BspTreeNode partitionRoom(Room room,int subdivisionLevel, int maxSubdivisionLevel){
        if(subdivisionLevel == maxSubdivisionLevel){
            return new BspTreeNode(room);
        }

        var rooms = room.getSizeX()*random.nextFloat(0.8f,1.2f) > room.getSizeZ() ? splitVertical(room) : splitHorizontal(room);

        Room roomLeft = rooms.getFirst();
        Room roomRight = rooms.getSecond();

        if (roomLeft.getSizeX() < 5 || roomLeft.getSizeZ() < 5 || roomRight.getSizeX() < 5 || room.getSizeZ() < 5) {
            return new BspTreeNode(room);
        }
        return new BspTreeNode(
                room,
                partitionRoom(rooms.getFirst(), subdivisionLevel+1,maxSubdivisionLevel),
                partitionRoom(rooms.getSecond(), subdivisionLevel+1,maxSubdivisionLevel)
        );
    }


    private Pair<Room> splitVertical(Room room){
        var roomSizeX = room.getEndX() - room.getStartX();

        var roomLeftEndX = room.getStartX()+  (int) (roomSizeX*random.nextFloat(0.3f,0.7f));

        var roomLeft = new Room(room.getStartX(), room.getStartY(), room.getStartZ(), roomLeftEndX, random.nextInt(room.getStartY()+3,room.getStartY()+5),room.getEndZ() );
        var roomRight = new Room(roomLeftEndX, room.getStartY(), room.getStartZ(), room.getEndX(), random.nextInt(room.getStartY()+3,room.getStartY()+5),room.getEndZ() );
        return new Pair<>(roomLeft,roomRight);
    }

    private Pair<Room> splitHorizontal(Room room){
        var roomSizeZ = room.getEndZ() - room.getStartZ();

        var roomLeftEndZ = room.getStartZ()+ (int) (roomSizeZ*random.nextFloat(0.3f,0.7f));

        var roomLeft = new Room(room.getStartX(), room.getStartY(), room.getStartZ(), room.getEndX(), random.nextInt(room.getStartY()+3,room.getStartY()+5),roomLeftEndZ );
        var roomRight = new Room(room.getStartX(), room.getStartY(), roomLeftEndZ, room.getEndX(), random.nextInt(room.getStartY()+3,room.getStartY()+5),room.getEndZ() );
        return new Pair<>(roomLeft,roomRight);
    }

    private List<Room> getRooms(List<Room> rooms, BspTreeNode bspTree){
        if(bspTree.getLeftChild() == null && bspTree.getRightChild() == null ){
            var room = bspTree.getRoom();
            shrinkRoom(room);
            rooms.add(room);
            return rooms;
        }
        getRooms(rooms, bspTree.getLeftChild());
        getRooms(rooms, bspTree.getRightChild());
        return rooms;
    }

    private void shrinkRoom(Room room){
        var optionalRoomSpaceX = room.getSizeX() - 5-1;
        var optionalRoomSpaceZ = room.getSizeZ() - 5-1;

        if(optionalRoomSpaceX > 1) {
            if (random.nextBoolean()){
                room.setStartX(room.getStartX() + random.nextInt(1,optionalRoomSpaceX));
                room.setEndX(room.getEndX() - 1 );
            }else {
                room.setStartX(room.getStartX() + 1);
                room.setEndX(room.getEndX() - random.nextInt(1,optionalRoomSpaceX) );
            }
        }else {
            room.setStartX(room.getStartX() + 1);
            room.setEndX(room.getEndX() - 1);
        }

        if(optionalRoomSpaceZ > 1) {
            if(random.nextBoolean()) {
                room.setStartZ(room.getStartZ() + random.nextInt(1,optionalRoomSpaceZ));
                room.setEndZ(room.getEndZ() - 1 );
            }else {
                room.setStartZ(room.getStartZ() + random.nextInt(1,optionalRoomSpaceZ));
                room.setEndZ(room.getEndZ() - 1 );
            }
        } else {
            room.setStartZ(room.getStartZ() + 1);
            room.setEndZ(room.getEndZ() - 1);
        }
    }

    public Vector3i getRoomConnectionPoint(Room room) {
        return new Vector3i( // the +1 is so that it doesnt connect to the corner (wall)
                room.getStartX() + room.getSizeX()/2,
                room.getStartY(),
                room.getStartZ() + room.getSizeZ()/2
        );
    }
}
