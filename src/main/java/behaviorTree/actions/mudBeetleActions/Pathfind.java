package behaviorTree.actions.mudBeetleActions;

import behaviorTree.NodeAction;
import behaviorTree.NodeCompletionStatus;
import behaviorTree.context.Context;
import behaviorTree.context.MudBeetleContext;
import com.jme3.math.Vector3f;
import pathfinding.AStar;
import server.ServerGameAppState;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import static behaviorTree.NodeCompletionStatus.RUNNING;
import static behaviorTree.NodeCompletionStatus.SUCCESS;

public class Pathfind extends NodeAction{
        private final Random random = new Random();
    
        private static final int BLOCK_SIZE = ServerGameAppState.getInstance().getBLOCK_SIZE();

        @Override
        public NodeCompletionStatus execute(Context context) {
            var hc = (MudBeetleContext) context;
            if (hc.getPathfindingFuture() == null) {
                decidePathfinding(hc);
            } else if (isPathReady(hc)) {
                startFollowingPath(hc);
                return SUCCESS;
            }
            return RUNNING;
        }

        public void pathfindClose(MudBeetleContext context) {
            var x = random.nextInt(-5 * BLOCK_SIZE, 6 * BLOCK_SIZE);
            var y = 0;
            var z = random.nextInt(-5 * BLOCK_SIZE, 6 * BLOCK_SIZE);

            var targetX = x;
            var targetY = y;
            var targetZ = z;
            Callable<List<Vector3f>> c = () -> {
                var mobPos = context.getMudBeetle().getNode().getWorldTranslation();

                var path = AStar.findPath(mobPos, mobPos.add(targetX, targetY, targetZ));
                return setRandomDestinationFromCellCenter(path);
            };

            Future pf = ServerGameAppState.pathfindingExecutor.submit(c);
//            Future pf = Main.getInstance().enqueue(c);

            context.setPathfindingFuture(pf);
        }

        public void pathfindToLastKnownTargetLocation(MudBeetleContext context) {
            var pathfindPos = context.getLastKnownTargetPosition();
            context.setLastKnownTargetPosition(null);
            Callable<List<Vector3f>> c = () -> {
                var mobPos = context.getMudBeetle().getNode().getWorldTranslation();
                var path = AStar.findPath(mobPos, pathfindPos);
                return path;
            };
            Future pf = ServerGameAppState.pathfindingExecutor.submit(c);
//            Future pf = Main.getInstance().enqueue(c);

            context.setPathfindingFuture(pf);

        }

        private boolean isPathReady(MudBeetleContext hc) {
            return hc.getPathfindingFuture() != null && hc.getPathfindingFuture().isDone();
        }

        private void startFollowingPath(MudBeetleContext hc) {
            try {
                var future = hc.getPathfindingFuture();
                var futurePath = future.get();

                List<Vector3f> path = null;
                if (futurePath != null) {
                    path = (List<Vector3f>) futurePath;
                }
                hc.setCurrentPath(path);
                hc.setPathfindingFuture(null);

            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(Pathfind.class.getName()).log(Level.SEVERE, "Error starting to follow path", ex);
            }
        }

        private void decidePathfinding(MudBeetleContext hc) {
            if (hc.getLastKnownTargetPosition() == null) {
                pathfindClose(hc);
            }
            if (hc.getLastKnownTargetPosition() != null) {
                pathfindToLastKnownTargetLocation(hc);
//            System.out.println("pathing to last known...");
            }
        }

        private List<Vector3f> setRandomDestinationFromCellCenter(List<Vector3f> path) {
            if (path == null) {
                return null;
            }
            var cellCenter = path.get(path.size() - 1);
            var hitboxSize = 0.5f+0.1f; /*hitbox width + offset to avoid getting stuck in float imprecision. Remember that this assumes
            that the last node lies exactly on cell center (check path smoothing etc..)*/
            cellCenter.addLocal(
                    random.nextFloat(-(BLOCK_SIZE / 2 - hitboxSize), (BLOCK_SIZE / 2 - hitboxSize)), 
                    0, 
                    random.nextFloat(-(BLOCK_SIZE / 2 - hitboxSize), (BLOCK_SIZE / 2 - hitboxSize))
            );
            return path;
        }

}
