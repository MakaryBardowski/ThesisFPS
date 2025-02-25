package game.map.proceduralGeneration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@AllArgsConstructor
@ToString
public class Room{
    private int startX,startY,startZ;
    private int endX,endY,endZ;

    public int getSizeX(){
        return endX - startX;
    }

    public int getSizeY(){
        return endY - startY;
    }

    public int getSizeZ(){
        return endZ - startZ;
    }
}
