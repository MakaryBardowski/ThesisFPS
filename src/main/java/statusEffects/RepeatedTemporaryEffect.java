package statusEffects;

public abstract class RepeatedTemporaryEffect extends TemporaryEffect{
    protected int ticksPerProc;
    
    public RepeatedTemporaryEffect(String name, EffectProcType procType,int maxTicks, int ticksPerProc) {
        super(name, procType,maxTicks);
        this.ticksPerProc = ticksPerProc;
    }
}
