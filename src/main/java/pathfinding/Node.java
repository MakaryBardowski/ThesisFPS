package pathfinding;

import com.jme3.math.Vector3f;

public class Node {

    int x, y, z;
    int g, h; // Cost from start, Heuristic
    Node parent;

    public Node(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public void setPosition(int x,int y, int z){
    this.x = x;
    this.y = y;
    this.z = z;
    }

    public int getF() {
        return g + h;
    }

    public Vector3f getVector3f() {
        return new Vector3f(x+0.5f, y, z+0.5f);
    }

}
