package menu;

import com.jme3.scene.Node;
import lombok.Getter;
import lombok.Setter;
import menu.states.MenuState;

import java.util.Stack;

public class MenuStateMachine {
    private Stack<MenuState> states = new Stack<>();

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
        states.push(null);
    }

    public synchronized void requestState(MenuState newState) {
        if(!canTransitionToState(newState)){
            return;
        }
        forceCloseCurrentState();
        setState(newState);

        if (getCurrentState() != null) {
            getCurrentState().open(guiNode, resolutionX, resolutionY);
        }
    }

    public synchronized void forceState(MenuState newState) {
        forceCloseCurrentState();
        setState(newState);
        if (getCurrentState() != null) {
            getCurrentState().open(guiNode, resolutionX, resolutionY);
        }
    }

    public synchronized void requestPreviousState(){
        if(!canTransitionToState(getPreviousState())){
            return;
        }
        forceCloseCurrentState();
        requestState(popCurrentState());
    }

    public synchronized boolean isStateNull(){
        return getCurrentState() == null;
    }

    private void setState(MenuState newState){
        states.push(newState);
    }

    public MenuState getCurrentState(){
        if(states.size() == 0){
            return null;
        }
        return states.peek();
    }

    private MenuState popCurrentState(){
        if(states.size() == 0){
            return null;
        }
        return states.pop();
    }

    private MenuState getPreviousState(){
        if(states.size() == 1){
            return null;
        }
        return states.get(states.size()-1);
    }

    private boolean canTransitionToState(MenuState newState){
        if(getCurrentState() == null){
            return true;
        }
        return getCurrentState().isTransitionAllowed(newState);
    }

    private void forceCloseCurrentState(){
        if (getCurrentState() != null) {
            getCurrentState().close();
        }
        popCurrentState();
    }

}
