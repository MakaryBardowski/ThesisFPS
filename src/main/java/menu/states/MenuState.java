package menu.states;

import com.jme3.scene.Node;

public interface MenuState {
    void close();
    void open(Node guiNode,float resolutionX, float resolutionY);
    MenuState onDuplicateStateRequest(MenuState requestedState);

    boolean isTransitionAllowed(MenuState newState);
}
