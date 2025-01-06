package statusEffects;

import game.entities.StatusEffectContainer;
import lombok.Getter;

public abstract class StatusEffect {

    @Getter
    protected String name;

    protected EffectSource source;

    @Getter
    protected EffectProcType procType;

    @Getter
    protected StatusEffectContainer owner;

    @Getter
    protected boolean unique;

    public StatusEffect(String name, EffectProcType procType) {
        this.name = name;
        this.procType = procType;
    }

    public abstract boolean updateServer();

    public abstract boolean updateClient();

    protected enum EffectSource {
        BARBED_WIRE_BLEED,
        SLOW,
        REGENERATION,
    }

    public int getEffectId() {
        return source.ordinal();
    }

}
