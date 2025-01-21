package menu.states;

import com.jme3.scene.Node;
import game.entities.mobs.player.Player;

public class InventoryMenuState implements MenuState {
    private final Player player;

    public InventoryMenuState(Player player){
        this.player = player;
    }

    @Override
    public void close() {
        player.getPlayerinventoryGui().toggle();
    }

    @Override
    public void open(Node guiNode, float resolutionX, float resolutionY) {
        player.getPlayerinventoryGui().toggle();
    }

    @Override
    public MenuState onDuplicateStateRequest(MenuState requestedState) {
        return requestedState;
    }

    @Override
    public boolean isTransitionAllowed(MenuState newState) {
        return true;
    }
}
