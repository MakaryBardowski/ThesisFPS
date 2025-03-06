package statusEffects.onHitEffects;

import com.jme3.network.AbstractMessage;
import data.DamageReceiveData;
import game.entities.Destructible;
import game.entities.StatusEffectContainer;
import server.ServerGameAppState;
import statusEffects.EffectProcType;

public class CutDownCardEffect extends OnHitEffect{
    private float EXECUTE_THRESHOLD = 7.5f/100;

    public CutDownCardEffect(int id, String name, StatusEffectContainer target, EffectProcType procType) {
        super(id, name, target, procType);
    }

    @Override
    public DamageReceiveData applyServer(DamageReceiveData input) {
        var serverLevelManager = ServerGameAppState.getInstance().getCurrentGamemode().getLevelManager();
        var attacker = (Destructible) serverLevelManager.getEntitiesById().get(input.getAttackerId());

        var victim = (Destructible) serverLevelManager.getEntitiesById().get(input.getVictimId());

        if( ( victim.getHealth() - victim.calculateDamage(input.getRawDamage()) ) <= attacker.getMaxHealth()*EXECUTE_THRESHOLD){
            input.setRawDamage(victim.getMaxHealth()*99);
        }
        return input;
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
