package statusEffects.onDamagedEffects;

import com.jme3.network.AbstractMessage;
import data.DamageReceiveData;
import game.entities.StatusEffectContainer;
import statusEffects.EffectProcType;
import statusEffects.StatusEffect;

public abstract class OnDamagedEffect extends StatusEffect<DamageReceiveData,DamageReceiveData> {
    public OnDamagedEffect(int id, String name, StatusEffectContainer target, EffectProcType procType) {
        super(id, name, target, procType);
    }
}
