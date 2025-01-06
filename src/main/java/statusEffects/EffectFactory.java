package statusEffects;

import game.entities.StatusEffectContainer;

public class EffectFactory {

    public static DamageOverTimeEffect createBleedEffect(StatusEffectContainer target, float damage, int maxTicks, int procsEvery) {
        var dot = new DamageOverTimeEffect("Bleed", EffectProcType.PERIODICAL, maxTicks, procsEvery, damage);
        dot.setTarget(target);
        return dot;
    }

    public static HealOverTimeEffect createRegenerationEffect(StatusEffectContainer target, float heal, int procsEvery) {
        var dot = new HealOverTimeEffect("Default Regen", EffectProcType.PERIODICAL, -1, procsEvery, heal);
        dot.setTarget(target);
        return dot;
    }

}
