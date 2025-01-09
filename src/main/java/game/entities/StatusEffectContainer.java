package game.entities;

import com.jme3.scene.Node;

import java.util.HashSet;
import lombok.Getter;
import statusEffects.StatusEffect;
import statusEffects.onDamagedEffects.OnDamagedEffect;
import statusEffects.onHitEffects.OnHitEffect;
import statusEffects.temporaryEffects.TemporaryEffect;

@Getter
public abstract class StatusEffectContainer extends Destructible {

    protected HashSet<OnHitEffect> onDealDamageEffects = new HashSet<>(10);
    protected HashSet<OnDamagedEffect> onDamageReceivedEffects = new HashSet<>(10);
    protected HashSet<TemporaryEffect> temporaryEffects = new HashSet<>(10);

    public StatusEffectContainer(int id, String name, Node node) {
        super(id, name, node);
    }

    public void addEffect(StatusEffect effect) {
        if(effect instanceof TemporaryEffect temporaryEffect){
            if (!temporaryEffect.isUnique() || (temporaryEffect.isUnique() && temporaryEffectsNotContain(temporaryEffect))) {
                temporaryEffects.add(temporaryEffect);
            }
        } else if (effect instanceof OnHitEffect onHitEffect){
            onDealDamageEffects.add(onHitEffect);
        } else if (effect instanceof OnDamagedEffect onDamagedEffect){
            onDamageReceivedEffects.add(onDamagedEffect);
        }
    }

    public void updateTemporaryEffectsServer() {
        var it = temporaryEffects.iterator();
        while (it.hasNext()) {
            var e = it.next();
            if (e.shouldBeRemoved()) {
                it.remove();
            }
            e.applyServer(null);
        }
    }

    public void updateTemporaryEffectsClient() {
        var it = temporaryEffects.iterator();
        while (it.hasNext()) {
            var e = it.next();
            e.applyClient(null);
        }
    }

    private boolean temporaryEffectsNotContain(StatusEffect effect) {
        for (StatusEffect e : temporaryEffects) {
            if (e.getEffectId() == effect.getEffectId()) {
                return false;
            }
        }

        return true;
    }
}
