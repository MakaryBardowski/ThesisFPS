package game;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import data.DamageReceiveData;
import game.entities.Entity;
import game.entities.mobs.Mob;
import lombok.Getter;

@Getter
public abstract class AttachedEntity extends Entity {
    protected Node node;

    public abstract void onInteract();


    public AttachedEntity(int id, String name, Node node) {
        super(id, name);
        this.node = node;
    }

    public abstract void setPosition(Vector3f newPos);
    public abstract void setPositionServer(Vector3f newPos);
}