package statusEffects.temporaryEffects;

import com.jme3.network.AbstractMessage;
import game.entities.Destructible;
import game.entities.StatusEffectContainer;
import game.map.collision.WorldGrid;
import lombok.Getter;
import lombok.Setter;
import messages.DestructibleDamageReceiveMessage;
import server.ServerMain;
import statusEffects.EffectProcType;
import statusEffects.temporaryEffects.RepeatedTemporaryEffect;

public class DamageOverTimeEffect extends RepeatedTemporaryEffect {

    @Getter
    @Setter
    private Destructible target;
    
    @Getter
    private final float damage;

    public DamageOverTimeEffect(int id, String name, StatusEffectContainer target, EffectProcType procType, int maxTicks, int activationsOverTime, float damage) {
        super(id, name, target, procType, maxTicks, activationsOverTime);
        this.damage = damage;
        unique = true;
        source = EffectSource.BARBED_WIRE_BLEED;
    }

    @Override
    public Void applyClient(Void input) {
        return null;
    }

    @Override
    public boolean shouldBeRemoved() {
        return maxTicks > 0 && ticks > maxTicks;
    }

    @Override
    public Void applyServer(Void input) {
        ticks++;

        if (ticks % (ticksPerProc) == 0) {

            target.setHealth(target.getHealth() - target.calculateDamage(damage));

            if (target.getHealth() <= 0) {
                WorldGrid grid = ServerMain.getInstance().getGrid();
                grid.remove(target);
                ServerMain.getInstance().getLevelManagerMobs().remove(target.getId());

            }

            DestructibleDamageReceiveMessage hpUpd = new DestructibleDamageReceiveMessage(target.getId(),target.getId(), damage);
            hpUpd.setReliable(true);
            ServerMain.getInstance().getServer().broadcast(hpUpd);
        }

        return null;
    }

    @Override
    public AbstractMessage createNewEntityMessage() {
        return null;
    }
}
