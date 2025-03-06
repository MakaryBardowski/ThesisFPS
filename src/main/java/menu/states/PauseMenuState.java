package menu.states;

import client.Main;
import client.appStates.ClientGameAppState;
import client.appStates.MainMenuAppState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.simsilica.lemur.GuiGlobals;
import game.cameraAndInput.InputController;
import game.entities.mobs.player.Player;
import menu.menuComponents.pauseMenu.PauseMenuButtonComponent;
import server.ServerGameAppState;

import java.util.ArrayList;
import java.util.List;

public class PauseMenuState implements MenuState{
    private List<PauseMenuButtonComponent> buttons = new ArrayList<>(2);
    @Override
    public void close() {
        Player.enablePlayerControls();
        GuiGlobals.getInstance().setCursorEventsEnabled(false,true);

        for(var button : buttons){
            button.removeFromParent();
        }
    }

    @Override
    public void open(Node guiNode, float resolutionX, float resolutionY) {
        Player.disablePlayerControls();
        GuiGlobals.getInstance().setCursorEventsEnabled(true,true);

        var player = ClientGameAppState.getInstance().getPlayer();
        if(player != null){
            player.setForward(false);
            player.setBackward(false);
            player.setLeft(false);
            player.setRight(false);
        }

        float buttonMarginX = resolutionX*0.5f;
        float buttonMarginY = resolutionY*0.75f;

        float buttonSizeX = resolutionX*0.3f;
        float buttonSizeY = resolutionY*0.12f;
        float spaceBetweenButtons = resolutionX*0.03f;

        Runnable quit = () -> {
            System.exit(0);
        };


        Runnable quitToMainMenu = () -> {
            Main.getInstance().enqueue( () -> {
                var gs = ClientGameAppState.getInstance();
                InputController.destroyKeys(Main.getInstance().getInputManager());
                var stateMgr = Main.getInstance().getStateManager();

                var gameState = stateMgr.getState(ClientGameAppState.class);
                stateMgr.detach(gameState);

                MainMenuAppState mms = new MainMenuAppState(Main.getInstance().getAssetManager(), Main.getInstance().getInputManager(), Main.getInstance().getAudioRenderer(), Main.getInstance().getGuiViewPort());
                stateMgr.attach(mms);
                gs.getMenuStateMachine().requestState(null);
                // if you try to start new game too fast the then sometimes port may not be free again yet (fix later)
                if (ServerGameAppState.getInstance() != null) {
                    var serverState = stateMgr.getState(ServerGameAppState.class);
                    stateMgr.detach(serverState);
                }
            });
        };

        Runnable returnToGame = () -> {
            ClientGameAppState.getInstance().getMenuStateMachine().requestState(null);
        };

        var buttonPos1 = new Vector3f(buttonMarginX-buttonSizeX/2, buttonMarginY,0);
        var buttonPos2 = new Vector3f(buttonMarginX-buttonSizeX/2,buttonMarginY-buttonSizeY-spaceBetweenButtons,0);
        var buttonPos3 = new Vector3f(buttonMarginX-buttonSizeX/2,buttonMarginY-3*buttonSizeY-3*spaceBetweenButtons,0);

        var card1 = new PauseMenuButtonComponent(buttonSizeX, buttonSizeY, buttonPos1, returnToGame,"Continue");
        var card2 = new PauseMenuButtonComponent(buttonSizeX,buttonSizeY, buttonPos2,quitToMainMenu,"Main Menu");
        var card3 = new PauseMenuButtonComponent(buttonSizeX,buttonSizeY, buttonPos3,quit,"Quit to desktop");

        buttons.add(card1);
        buttons.add(card2);
        buttons.add(card3);

        guiNode.attachChild(card1);
        guiNode.attachChild(card2);
        guiNode.attachChild(card3);
    }

    @Override
    public boolean isTransitionAllowed(MenuState newState) {
        return true;
    }
}
