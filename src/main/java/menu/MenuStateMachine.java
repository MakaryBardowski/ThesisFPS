package menu;

import com.jme3.scene.Node;
import lombok.Getter;
import lombok.Setter;
import menu.states.MenuState;

public class MenuStateMachine {
    private MenuState currentState;

    private final Node guiNode;

    @Getter
    @Setter
    private float resolutionX;

    @Getter
    @Setter
    private float resolutionY;

    public MenuStateMachine(Node guiNode, float resolutionX, float resolutionY){
        this.guiNode = guiNode;
        this.resolutionX = resolutionX;
        this.resolutionY = resolutionY;
    }

    public synchronized void requestState(MenuState newState) {
        if (currentState != null) {

            if(newState != null && currentState.getClass().equals(newState.getClass())){
                newState = currentState.onDuplicateStateRequest(newState);
            }

            if (!currentState.isTransitionAllowed(newState)) {
                return;
            }
            currentState.close();
        }
        currentState = newState;

        if (currentState != null) {
            currentState.open(guiNode, resolutionX, resolutionY);
        }
    }


    public synchronized void forceState(MenuState newState) {
        if (currentState != null) {
            currentState.close();
        }
        currentState = newState;
        if (currentState != null) {
            currentState.open(guiNode, resolutionX, resolutionY);
        }
    }

    public synchronized boolean isStateNull(){
        return currentState == null;
    }
}
