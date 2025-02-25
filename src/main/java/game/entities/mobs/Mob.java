package game.entities.mobs;

import behaviorTree.BehaviorTree;
import data.DamageReceiveData;
import game.entities.Destructible;
import game.entities.factories.MobSpawnType;
import game.entities.inventory.Equipment;
import game.items.Item;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import game.entities.Animated;
import game.entities.Animation;
import game.entities.Collidable;
import game.entities.FloatAttribute;
import game.entities.StatusEffectContainer;
import game.map.collision.WorldGrid;
import java.util.ArrayList;
import java.util.Random;
import lombok.Getter;
import lombok.Setter;
import settings.GlobalSettings;

public abstract class Mob extends StatusEffectContainer implements MobInterface, Animated {
    public static final int SPEED_ATTRIBUTE = 2;

    protected static final float MOB_ROTATION_RATE = 6f;
    protected static final int EQUIPMENT_SIZE = 20;

    private static final float DEFAULT_SPEED = 8.75f;

    protected final MobSpawnType mobSpawnType; // original template that spawned this mob

    @Getter
    protected BehaviorTree behaviorTree;

    protected Equipment equipment = new Equipment(new Item[EQUIPMENT_SIZE]); // 6 rows 3 cols

    //mob stats
    protected float cachedSpeed = DEFAULT_SPEED; // speed is very frequently accessed
    // so it has to be cached whenever it is changed

    //mob ai variables
    protected Destructible currentTarget;

    @Getter
    protected Vector3f serverLocation; // updated by the server

    @Getter
    protected Quaternion serverRotation;

    @Getter
    @Setter
    protected float posInterpolationValue;

    @Getter
    @Setter
    protected float rotInterpolationValue;

    public Mob(MobSpawnType mobSpawnType, int id, Node node, String name) {
        super(id, name, node);
        this.mobSpawnType = mobSpawnType;
        this.serverLocation = node.getWorldTranslation();
        this.serverRotation = node.getLocalRotation();
        attributes.put(SPEED_ATTRIBUTE, new FloatAttribute(cachedSpeed));
    }

    public boolean doesNotCollideWithEntitiesAtPositionServer(Vector3f newPos, WorldGrid grid, ArrayList<Collidable> solidCollidables) {
        for (Collidable m : solidCollidables) {
            if (this != m && collisionShape.wouldCollideAtPosition(m.getCollisionShape(), newPos)) {
                return false;
            }
        }
        return true;
    }

    public void checkPassableCollisionsServer(WorldGrid grid, ArrayList<Collidable> passableCollidables) {
        for (Collidable m : passableCollidables) {
            if (this != m && collisionShape.wouldCollideAtPosition(m.getCollisionShape(), this.getNode().getWorldTranslation())) {
                m.onCollisionServer(this);
            }
        }
    }

    public float getCachedSpeed() {
        return cachedSpeed;
    }

    public void setCachedSpeed(float cachedSpeed) {
        this.cachedSpeed = cachedSpeed;
    }

    public boolean isDead() {
        return getHealth() <= 0;
    }

    public Destructible getCurrentTarget() {
        return currentTarget;
    }

    public void setCurrentTarget(Destructible currentTarget) {
        this.currentTarget = currentTarget;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setServerLocation(Vector3f serverLocation) {
        this.serverLocation = serverLocation;
        posInterpolationValue = 0;
    }

    public void setServerRotation(Quaternion serverRotation) {
        this.serverRotation = serverRotation;
        this.rotInterpolationValue = 0;
    }

    public abstract void interpolateRotation(float tpf);

    protected void dropEquipment() {
        Random r = new Random();
        equipment.removeAllItems().forEach(item -> {
            item.drop(node.getWorldTranslation().add(r.nextFloat(-0.25f, 0.25f), 2 + r.nextFloat(-1, 1), r.nextFloat(-0.25f, 0.25f)),
                    new Vector3f(r.nextFloat(-2,2),r.nextFloat(3,8),r.nextFloat(-2,2))
                    );
        });
    }

    @Override
    public void updateAi() {
        if (GlobalSettings.isAiDebug) {
            System.out.println("trying to update " + this);
        }
        if (behaviorTree != null) {
            behaviorTree.update();
        }
    }

    @Override
    public boolean isAbleToMove() {
        return !isDead();
    }

    @Override
    public void playAnimation(Animation anim) {}

    public abstract void dealDamageClient(float damage, Destructible target);
    public abstract void dealDamageServer(DamageReceiveData damageReceiveData, Destructible target);
}
