package statusEffects;

import game.entities.Destructible;
import game.map.collision.WorldGrid;
import lombok.Getter;
import lombok.Setter;
import messages.DestructibleDamageReceiveMessage;
import server.ServerMain;

public class DamageOverTimeEffect extends RepeatedTemporaryEffect {

    @Getter
    @Setter
    private Destructible target;
    
    @Getter
    private final float damage;

    public DamageOverTimeEffect(String name, EffectProcType procType, int maxTicks, int activationsOverTime, float damage) {
        super(name, procType, maxTicks, activationsOverTime);
        this.damage = damage;
        unique = true;
        source = EffectSource.BARBED_WIRE_BLEED;
    }

    @Override
    public boolean updateClient() {
        ticks++;
        if (ticks > maxTicks) {
            boolean readyToRemove = true;
            return readyToRemove;
        } else if (ticks % (ticksPerProc) == 0) {
//            System.out.println("The " + target + " bleeds!");
        }
        return false;
    }

    @Override
    public boolean updateServer() {
        ticks++;
        if (ticks > maxTicks) {
            boolean readyToRemove = true;
            return readyToRemove;
        } else if (ticks % (ticksPerProc) == 0) {

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
        return false;
    }

}
