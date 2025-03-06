package behaviorTree.actions.mudBeetleActions;

import behaviorTree.NodeAction;
import behaviorTree.NodeCompletionStatus;
import behaviorTree.context.Context;
import behaviorTree.context.MudBeetleContext;
import server.ServerGameAppState;

import java.util.Random;

import static behaviorTree.NodeCompletionStatus.FAILURE;
import static behaviorTree.NodeCompletionStatus.SUCCESS;

public class ShouldLookIntoRandomDirection extends NodeAction {

    private Random random = new Random();

    @Override
    public NodeCompletionStatus execute(Context context) {
        var hc = (MudBeetleContext) context;
        hc.setLookInRandomDirectionTimer(hc.getLookInRandomDirectionTimer() - ServerGameAppState.getTimePerFrame());
        if (hc.getCurrentPath() == null && hc.getCurrentTarget() == null && hc.getLookInRandomDirectionTimer() <= 0) {
            return SUCCESS;
        }
        return FAILURE;
    }
}
