package game.entities;

import com.jme3.scene.Node;
import data.DamageReceiveData;
import game.entities.mobs.Mob;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Destructible extends Collidable {

    public static final int HEALTH_ATTRIBUTE = 0;
    public static final int MAX_HEALTH_ATTRIBUTE = 1;

    protected float initialHealth = 12;
    protected float initialMaximumHealth = 12;

    public Destructible(int id, String name, Node node) {
        super(id, name, node);
        setHealth(initialHealth);
        setMaxHealth(initialHealth);
    }

    public abstract void onAttacked(Mob shooter, DamageReceiveData damage);


    public void onDeathServer() {};
    public void onDeathClient() {};

    public abstract void receiveDamageClient(DamageReceiveData damageData);
    public abstract void receiveDamageServer(DamageReceiveData damageData);
    
    public abstract void notifyServerAboutReceivingDamage(DamageReceiveData damageReceiveData);

    public void receiveHealClient(float heal) {
        setHealth(getHealth()+heal);
    }

    public abstract void die();

    public abstract float getArmorValue();

    public abstract float calculateDamage(float damage);

    public float getMaxHealth(){
        return ((FloatAttribute)attributes.get(MAX_HEALTH_ATTRIBUTE)).getValue();
    }

    public void setMaxHealth(float value){
        attributes.put(MAX_HEALTH_ATTRIBUTE,new FloatAttribute(value));
    }

    public float getHealth(){
        return ((FloatAttribute)attributes.get(HEALTH_ATTRIBUTE)).getValue();
    }

    public void setHealth(float value){
        attributes.put(HEALTH_ATTRIBUTE,new FloatAttribute(value));
    }

}