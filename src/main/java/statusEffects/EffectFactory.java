package statusEffects;

import game.entities.StatusEffectContainer;
import server.ServerMain;
import statusEffects.onHitEffects.CutDownCardEffect;
import statusEffects.onHitEffects.DeadeyeCardEffect;
import statusEffects.onHitEffects.OnHitEffect;
import statusEffects.temporaryEffects.DamageOverTimeEffect;
import statusEffects.temporaryEffects.HealOverTimeEffect;

public class EffectFactory {
    private static final String INVALID_EFFECT_ID_PROVIDED = "invalid effect id '%s' provided.";

    public static StatusEffect createEffect(EffectTemplates.EffectTemplate effectTemplate, int newEffectId, StatusEffectContainer target){
        if(effectTemplate == EffectTemplates.DEFAULT_REGENERATION){
            var procsEverySeconds = 15;
            return createRegenerationEffect(newEffectId,"Default regeneration",target,1,-1,procsEverySeconds*ServerMain.getInstance().getTICKS_PER_SECOND());
        } else if (effectTemplate == EffectTemplates.DEADEYE_CARD_CRIT){
            return createDeadeyeCritChanceEffect(newEffectId,"Deadeye card effect",target);
        } else if (effectTemplate == EffectTemplates.CUTDOWN_CARD_EXECUTE){
            return createCutDownExecuteEffect(newEffectId,"Cutdown card effect",target);
        }

        throw new IllegalArgumentException(String.format(INVALID_EFFECT_ID_PROVIDED,effectTemplate));
    }

    public static HealOverTimeEffect createRegenerationEffect(int id, String name, StatusEffectContainer target, float heal, int maxTicks, int procsEvery) {
        var dot = new HealOverTimeEffect(id,name, target, EffectProcType.PERIODICAL, maxTicks, procsEvery, heal);
        return dot;
    }

    public static OnHitEffect createDeadeyeCritChanceEffect(int id, String name, StatusEffectContainer target) {
        return new DeadeyeCardEffect(id,name, target, EffectProcType.ON_HIT);
    }

    public static OnHitEffect createCutDownExecuteEffect(int id, String name, StatusEffectContainer target) {
        return new CutDownCardEffect(id,name, target, EffectProcType.ON_HIT);
    }

    public static DamageOverTimeEffect createBleedEffect(int id, StatusEffectContainer target, float damage, int maxTicks, int procsEvery) {
        var dot = new DamageOverTimeEffect(id,"Bleed", target, EffectProcType.PERIODICAL, maxTicks, procsEvery, damage);
        dot.setTarget(target);
        return dot;
    }
}
