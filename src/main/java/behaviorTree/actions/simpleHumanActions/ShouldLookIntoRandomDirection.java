package behaviorTree.actions.simpleHumanActions;

import behaviorTree.NodeAction;
import behaviorTree.NodeCompletionStatus;
import static behaviorTree.NodeCompletionStatus.FAILURE;
import static behaviorTree.NodeCompletionStatus.SUCCESS;
import behaviorTree.context.Context;
import behaviorTree.context.SimpleHumanMobContext;

import java.util.Random;
import server.ServerGameAppState;

public class ShouldLookIntoRandomDirection extends NodeAction {

    private Random random = new Random();

    @Override
    public NodeCompletionStatus execute(Context context) {
        var hc = (SimpleHumanMobContext) context;
        hc.setLookInRandomDirectionTimer(hc.getLookInRandomDirectionTimer() - ServerGameAppState.getTimePerFrame());
        if (hc.getCurrentPath() == null && hc.getCurrentTarget() == null && hc.getLookInRandomDirectionTimer() <= 0) {
            return SUCCESS;
        }
        return FAILURE;
    }
}
