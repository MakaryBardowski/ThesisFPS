package statusEffects;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class EffectTemplates {
    private static final List<EffectTemplate> templates = new ArrayList<>();

    public static final EffectTemplate DEFAULT_REGENERATION = new EffectTemplate(1);
    public static final EffectTemplate DEADEYE_CARD_CRIT = new EffectTemplate(2);
    public static final EffectTemplate CUTDOWN_CARD_EXECUTE = new EffectTemplate(3);

    public static EffectTemplate getTemplateByIndex(int index){
        return templates.get(index);
    }

    @AllArgsConstructor
    @Getter
    public static class EffectTemplate{
        private int effectId;
    }
}
