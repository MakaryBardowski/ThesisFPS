package game.entities;

import client.appStates.ClientGameAppState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import game.entities.DecorationTemplates.DecorationTemplate;
import messages.gameSetupMessages.NextLevelMessage;

public class LevelExit extends IndestructibleDecoration {
    public LevelExit(int id, String name, Node node, DecorationTemplate template) {
        super(id, name, node, template);
    }

    @Override
    public void showHitboxIndicator() {
    }

    ;

    @Override
    public void onCollisionClient(Collidable other) {
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void onCollisionServer(Collidable other) {
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void moveClient(float tpf) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void onInteract() {

        int currentLevelIndex = ClientGameAppState.getInstance().getCurrentGamemode().getLevelManager().getCurrentLevelIndex();
        var jumpToLevelMessage = new NextLevelMessage(currentLevelIndex);
        ClientGameAppState.getInstance().getClient().send(jumpToLevelMessage);

        System.out.println("exiting the level...");
    }

    @Override
    public void setPositionClient(Vector3f newPos) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void setPositionServer(Vector3f newPos) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
