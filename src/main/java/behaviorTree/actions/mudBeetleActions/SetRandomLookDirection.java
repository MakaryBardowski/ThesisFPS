package behaviorTree.actions.mudBeetleActions;

import behaviorTree.NodeAction;
import behaviorTree.NodeCompletionStatus;
import behaviorTree.context.Context;
import behaviorTree.context.MudBeetleContext;
import com.jme3.math.Vector3f;

import java.util.Random;

import static behaviorTree.NodeCompletionStatus.SUCCESS;

public class SetRandomLookDirection extends NodeAction {

    private Random random = new Random();

    @Override
    public NodeCompletionStatus execute(Context context) {
        var hc = (MudBeetleContext) context;
        hc.putLookInRandomDirectionOnCooldown();
        hc.setDesiredLookDirection(new Vector3f(
                random.nextFloat(-1, 1), 0, random.nextFloat(-1, 1)
        )
        );

        return SUCCESS;
    }
}
