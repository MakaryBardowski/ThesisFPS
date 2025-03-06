package game.entities;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.Getter;
import lombok.Setter;

public abstract class Movable extends AttachedEntity {

    @Getter
    protected AtomicBoolean positionChangedOnServer = new AtomicBoolean(true);
    @Getter
    protected AtomicBoolean rotationChangedOnServer = new AtomicBoolean(true);
    
    @Getter
    @Setter
    protected Vector3f movementVector = new Vector3f(0, 0, 0);

    public Movable(int id, String name, Node node) {
        super(id, name, node);
    }

    public abstract void moveClient(float tpf);

    public abstract void moveServer(Vector3f moveVec);

    public boolean isAbleToMove() {
        return true;
    }
;
}
