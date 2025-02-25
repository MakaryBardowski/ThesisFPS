package statusEffects.onHitEffects;

import com.jme3.network.AbstractMessage;
import data.DamageReceiveData;
import game.entities.Destructible;
import game.entities.StatusEffectContainer;
import server.ServerGameAppState;
import statusEffects.EffectProcType;

import java.util.HashMap;
import java.util.Map;

public class OverfedCardEffect extends OnHitEffect{
    private static final float HEALTH_INCREASE = 10000.5f;

    public OverfedCardEffect(int id, String name, StatusEffectContainer target, EffectProcType procType) {
        super(id, name, target, procType);
    }

    @Override
    public DamageReceiveData applyServer(DamageReceiveData input) {
        var serverLevelManager = ServerGameAppState.getInstance().getCurrentGamemode().getLevelManager();
        var attacker = (Destructible) serverLevelManager.getEntitiesById().get(input.getAttackerId());
        var victim = (Destructible) serverLevelManager.getEntitiesById().get(input.getVictimId());

        if( ( victim.getHealth() - victim.calculateDamage(input.getRawDamage()) ) <= 0){
            attacker.setFloatAttributesAndNotifyClients(getHealthAndMaxHealthUpdate(
                    attacker.getFloatAttribute(Destructible.MAX_HEALTH_ATTRIBUTE_KEY).getValue()+HEALTH_INCREASE,
                    attacker.getFloatAttribute(Destructible.HEALTH_ATTRIBUTE_KEY).getValue()+HEALTH_INCREASE
            ));
        }
        return input;
    }

    private Map<Integer,Float> getHealthAndMaxHealthUpdate(float newMaxHealthValue, float newHealthValue){
        var updates = new HashMap<Integer,Float>();
        updates.put(Destructible.MAX_HEALTH_ATTRIBUTE_KEY,newMaxHealthValue);
        updates.put(Destructible.HEALTH_ATTRIBUTE_KEY,newHealthValue);
        return updates;
    }

    @Override
    public DamageReceiveData applyClient(DamageReceiveData input) {
        return null;
    }

    @Override
    public boolean shouldBeRemoved() {
        return false;
    }

    @Override
    public AbstractMessage createNewEntityMessage() {
        return null;
    }

}
