package statusEffects.onHitEffects;

import com.jme3.network.AbstractMessage;
import data.DamageReceiveData;
import game.entities.Destructible;
import game.entities.StatusEffectContainer;
import server.ServerMain;
import statusEffects.EffectProcType;

public class CutDownCardEffect extends OnHitEffect{
    private float EXECUTE_THRESHOLD = 15f/100;

    public CutDownCardEffect(int id, String name, StatusEffectContainer target, EffectProcType procType) {
        super(id, name, target, procType);
    }

    @Override
    public DamageReceiveData applyServer(DamageReceiveData input) {
        var serverLevelManager = ServerMain.getInstance().getCurrentGamemode().getLevelManager();
        var victim = (Destructible) serverLevelManager.getMobs().get(input.getVictimId());

        if( ( victim.getHealth() - victim.calculateDamage(input.getRawDamage()) ) <= victim.getMaxHealth()*EXECUTE_THRESHOLD){
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
