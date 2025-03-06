package statusEffects.onHitEffects;

import com.jme3.network.AbstractMessage;
import data.DamageReceiveData;
import game.entities.Destructible;
import game.entities.StatusEffectContainer;
import server.ServerGameAppState;
import statusEffects.EffectProcType;

import java.util.Random;

public class DeadeyeCardEffect extends OnHitEffect{
    private float CRIT_CHANCE = 0.03f;
    private float PERCENT_CHANCE_PER_TILE = 1;
    private static float CRIT_DAMAGE_MULTIPLIER = 2f;

    private Random random = new Random();

    public DeadeyeCardEffect(int id, String name, StatusEffectContainer target, EffectProcType procType) {
        super(id, name, target, procType);
    }

    @Override
    public DamageReceiveData applyServer(DamageReceiveData input) {
        var serverLevelManager = ServerGameAppState.getInstance().getCurrentGamemode().getLevelManager();
        var attacker = (Destructible) serverLevelManager.getEntitiesById().get(input.getAttackerId());
        var victim = (Destructible) serverLevelManager.getEntitiesById().get(input.getVictimId());

        // additional 1 percent per tile
        var additionalCritChanceForDistance = (
                (int)(attacker.getNode().getWorldTranslation().distance(victim.getNode().getWorldTranslation()) / serverLevelManager.getBLOCK_SIZE())
        )*(PERCENT_CHANCE_PER_TILE/100);
        var totalChance = CRIT_CHANCE + additionalCritChanceForDistance;

//        System.out.println("total chance is "+totalChance);
        if(random.nextFloat() <= totalChance){
            input.setRawDamage(input.getRawDamage() * CRIT_DAMAGE_MULTIPLIER);
        }
        return input;
    }

    @Override
    public DamageReceiveData applyClient(DamageReceiveData input) {
        return null;
    }

    @Override
    public boolean shouldBeRemoved() {
        return false;
    }

    @Override
    public AbstractMessage createNewEntityMessage() {
        return null;
    }
}
