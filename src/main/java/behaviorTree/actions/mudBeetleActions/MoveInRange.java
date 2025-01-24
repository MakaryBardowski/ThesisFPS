package behaviorTree.actions.mudBeetleActions;

import behaviorTree.NodeAction;
import behaviorTree.NodeCompletionStatus;
import behaviorTree.context.Context;
import behaviorTree.context.MudBeetleContext;
import client.Main;
import com.jme3.math.Vector3f;
import game.entities.Destructible;
import game.entities.mobs.MudBeetle;
import server.ServerMain;

import static behaviorTree.NodeCompletionStatus.STOP_TREE_EXECUTION;

public class MoveInRange extends NodeAction {

    private float TEST_WEAPON_RANGE = 1.5f-0.1f;
    private Vector3f moveVec;
    private Destructible target;
    private MudBeetle human;
    private MudBeetleContext hc;
    private Vector3f direction;

    @Override
    public NodeCompletionStatus execute(Context context) {
        hc = (MudBeetleContext) context;
        human = hc.getMudBeetle();

        if (human.isDead()) {
            return STOP_TREE_EXECUTION;
        }

        target = hc.getCurrentTarget();
        direction = target.getNode().getWorldTranslation()
                .subtract(human.getNode().getWorldTranslation());
        var moveDistance = human.getCachedSpeed() * ServerMain.getTimePerFrame();

        if (moveDistance > direction.length() - TEST_WEAPON_RANGE) {
            moveDistance = direction.length() - TEST_WEAPON_RANGE;
            //
                moveDistance = moveDistance < 0 ? 0 : moveDistance;
            //
        }

        moveVec = direction.normalizeLocal();
        moveVec.multLocal(moveDistance);

        Main.getInstance().enqueue(() -> {
            human.moveServer(moveVec);
        });

        if (human.getNode().getWorldTranslation().distance(target.getNode().getWorldTranslation()) <= TEST_WEAPON_RANGE) {
            return NodeCompletionStatus.SUCCESS;
        }

        return NodeCompletionStatus.FAILURE;
    }

}
