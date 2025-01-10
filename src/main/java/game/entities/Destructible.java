package game.entities;

import com.jme3.scene.Node;
import data.DamageReceiveData;
import game.entities.mobs.Mob;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Destructible extends Collidable {

    protected static final int HEALTH_ATTRIBUTE = 0;
    protected static final int MAX_HEALTH_ATTRIBUTE = 1;

    protected float health = 12;
    protected float maxHealth = 12;

    public Destructible(int id, String name, Node node) {
        super(id, name, node);
        attributes.put(HEALTH_ATTRIBUTE, new FloatAttribute(health));
        attributes.put(MAX_HEALTH_ATTRIBUTE, new FloatAttribute(maxHealth));
    }

    public abstract void onAttacked(Mob shooter, DamageReceiveData damage);


    public void onDeathServer() {};
    public void onDeathClient() {};

    public abstract void receiveDamageClient(DamageReceiveData damageData);
    public abstract void receiveDamageServer(DamageReceiveData damageData);
    
    public abstract void notifyServerAboutReceivingDamage(DamageReceiveData damageReceiveData);

    public void receiveHealClient(float heal) {
        health += heal;
    }

    public abstract void die();

    public abstract float getArmorValue();

    public abstract float calculateDamage(float damage);
}