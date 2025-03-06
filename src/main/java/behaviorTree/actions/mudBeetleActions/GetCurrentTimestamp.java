package behaviorTree.actions.mudBeetleActions;

import behaviorTree.NodeAction;
import behaviorTree.NodeCompletionStatus;
import behaviorTree.context.Context;
import behaviorTree.context.MudBeetleContext;
import behaviorTree.context.SimpleHumanMobContext;

import static behaviorTree.NodeCompletionStatus.SUCCESS;

public class GetCurrentTimestamp extends NodeAction{
    
        @Override
        public NodeCompletionStatus execute(Context context) {
            ((MudBeetleContext) context).setCurrentUpdateTimestamp(System.currentTimeMillis());
            return SUCCESS;
        }

}
