package statusEffects.onDamagedEffects;

import com.jme3.network.AbstractMessage;
import data.DamageReceiveData;
import game.entities.StatusEffectContainer;
import statusEffects.EffectProcType;

public class HardnessCardEffect extends OnDamagedEffect {
    private static final float FLAT_DAMAGE_BLOCK = 1f;

    public HardnessCardEffect(int id, String name, StatusEffectContainer target, EffectProcType procType) {
        super(id, name, target, procType);
    }

    @Override
    public DamageReceiveData applyServer(DamageReceiveData input) {
        input.setRawDamage(input.getRawDamage()-FLAT_DAMAGE_BLOCK);
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
