package client;

import game.items.Item;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import messages.items.MobItemInteractionMessage;
import messages.items.MobItemInteractionMessage.ItemInteractionType;

public class PlayerHUDController implements ScreenController {

    private static ClientGameAppState gs;

    public PlayerHUDController(ClientGameAppState gs) {
        this.gs = gs;
    }

    @Override
    public void bind(Nifty nifty, Screen screen) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onStartScreen() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void onEndScreen() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static void sendEquipMessageToServer(Item item){
            if (item != null) {
            MobItemInteractionMessage imsg = new MobItemInteractionMessage(item, gs.getPlayer(), ItemInteractionType.EQUIP);
            imsg.setReliable(true);
            gs.getClient().send(imsg);
        }
    }

}
