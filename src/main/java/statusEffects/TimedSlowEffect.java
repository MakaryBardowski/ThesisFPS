package statusEffects;

import game.entities.mobs.Mob;

public class TimedSlowEffect extends TemporaryEffect {

    private final Mob target;
    private final float speedBefore;

    public TimedSlowEffect(String name, EffectProcType procType, int maxTicks, Mob target,float slowStrength) {
        super(name, procType, maxTicks);
        this.target = target;
        speedBefore = target.getCachedSpeed();
        unique = true;
        source = EffectSource.SLOW;
        target.setCachedSpeed(calculateSpeedAfterSlow(slowStrength));
        
    }

    @Override
    public boolean updateServer() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean updateClient() {
        ticks++;
        if (ticks > maxTicks) {
            boolean readyToRemove = true;
            target.setCachedSpeed(speedBefore);
            return readyToRemove;
        }
        return false;
    }
    
    private float calculateSpeedAfterSlow(float slowStrength){
    return target.getCachedSpeed()-( target.getCachedSpeed() * 0.01f*slowStrength);
    }

}
