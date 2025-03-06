package game.map.proceduralGeneration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BspTreeNode {
    private Room room;
    private BspTreeNode leftChild;
    private BspTreeNode rightChild;

    public BspTreeNode(Room room){
        this.room = room;
    }

    public BspTreeNode(Room room, BspTreeNode leftChild, BspTreeNode rightChild){
        this(room);
        this.leftChild = leftChild;
        this.rightChild = rightChild;
    }

}
