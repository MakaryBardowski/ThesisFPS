package behaviorTree.actions.mudBeetleActions;

import behaviorTree.NodeAction;
import behaviorTree.NodeCompletionStatus;
import behaviorTree.context.Context;
import behaviorTree.context.MudBeetleContext;
import behaviorTree.context.SimpleHumanMobContext;
import client.Main;
import game.entities.Destructible;
import game.entities.mobs.MudBeetle;

import static behaviorTree.NodeCompletionStatus.FAILURE;

public class Attack extends NodeAction {

    private static final float SECONDS_TO_MILLIS = 1000;
    private long lastAttackedTimestamp = 0;
    private float ATTACK_COOLDOWN = 2;
    private float TEST_GUN_RANGE = 2;
    private float TEST_DAMAGE = 2f;
    private MudBeetle human;
    private Destructible target;

    @Override
    public NodeCompletionStatus execute(Context context) {
        var hc = (MudBeetleContext) context;
        human = (MudBeetle) context.getBlackboard().get(MudBeetleContext.STEERED_MUD_BEETLE);
        target = (Destructible) context.getBlackboard().get(SimpleHumanMobContext.TARGET_DESTRUCTIBLE);

            if (hc.getCurrentUpdateTimestamp() - lastAttackedTimestamp < ATTACK_COOLDOWN*SECONDS_TO_MILLIS ) {
                return FAILURE;
            }

            var distance = human.getNode().getWorldTranslation().distance(target.getNode().getWorldTranslation());
            if (distance > TEST_GUN_RANGE) {
                return NodeCompletionStatus.FAILURE;
            }
            Main.getInstance().enqueue(() -> {
                human.dealDamageClient(TEST_DAMAGE,target);
            });
            lastAttackedTimestamp = hc.getCurrentUpdateTimestamp();


        return NodeCompletionStatus.SUCCESS;
    }
}
