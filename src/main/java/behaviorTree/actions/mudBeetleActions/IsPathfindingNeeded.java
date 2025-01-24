package behaviorTree.actions.mudBeetleActions;

import behaviorTree.NodeAction;
import behaviorTree.NodeCompletionStatus;
import behaviorTree.context.Context;
import behaviorTree.context.MudBeetleContext;
import server.ServerMain;

public class IsPathfindingNeeded extends NodeAction {

    @Override
    public NodeCompletionStatus execute(Context context) {
        var hc = (MudBeetleContext) context;
        hc.setFindRandomPathTimer(hc.getFindRandomPathTimer() - ServerMain.getTimePerFrame());
        if (shouldPathfind(hc)) {
            hc.setLookInRandomDirectionTimer(hc.getLookInRandomDirectionCooldown());
            hc.setFindRandomPathTimer(hc.getFindRandomPathCooldown());
            return NodeCompletionStatus.SUCCESS;
        }
        return NodeCompletionStatus.FAILURE;
    }

    private boolean shouldPathfind(MudBeetleContext hc) {
        return (hc.getCurrentPath() == null
                && hc.getFindRandomPathTimer() < 0
                && hc.getCurrentTarget() == null)
                || hc.getLastKnownTargetPosition() != null /*
                always pathfind.
                 */;
    }
}
