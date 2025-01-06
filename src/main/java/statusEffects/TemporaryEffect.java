package statusEffects;

public abstract class TemporaryEffect extends StatusEffect {

    protected int ticks = -1; //effect starts immediately
    protected int maxTicks;

    public TemporaryEffect(String name, EffectProcType procType, int maxTicks) {
        super(name, procType);
        this.maxTicks = maxTicks;
    }



}
