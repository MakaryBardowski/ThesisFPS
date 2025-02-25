package statusEffects.onHitEffects;

import com.jme3.network.AbstractMessage;
import data.DamageReceiveData;
import game.entities.Destructible;
import game.entities.StatusEffectContainer;
import server.ServerGameAppState;
import statusEffects.EffectProcType;

import java.util.Random;

public class GoodLuckProtectionCardEffect extends OnHitEffect{
    private static final float MAX_MULTIPLIER = 1.2f;
    private static final float MIN_MULTIPLIER = 0.85f;
    private final Random random = new Random();

    public GoodLuckProtectionCardEffect(int id, String name, StatusEffectContainer target, EffectProcType procType) {
        super(id, name, target, procType);
    }

    @Override
    public DamageReceiveData applyServer(DamageReceiveData input) {
        var serverLevelManager = ServerGameAppState.getInstance().getCurrentGamemode().getLevelManager();
        var victim = (Destructible) serverLevelManager.getMobs().get(input.getVictimId());

        input.setRawDamage(input.getRawDamage()*random.nextFloat(MIN_MULTIPLIER,MAX_MULTIPLIER));
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
