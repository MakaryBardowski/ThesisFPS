package messages.items;

import client.ClientGameAppState;
import client.Main;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import game.items.Item;
import game.items.weapons.MeleeWeapon;
import lombok.Getter;
import server.ServerMain;

@Serializable
@Getter
public class NewMeleeWeaponMessage extends NewItemMessage {
    private int ammo;

    public NewMeleeWeaponMessage() {
    }

    public NewMeleeWeaponMessage(MeleeWeapon item) {
        super(item);
    }

    @Override
    public void handleServer(ServerMain server,HostedConnection hc) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void handleClient(ClientGameAppState client) {
            Item i = (Item) ifa.createItem(id, getTemplate(), droppable);
            if(isAlreadyDropped()){
                Main.getInstance().enqueue(()-> {
                    i.drop(getDroppedPosition());
                });
            }
            client.registerEntity(i);
    }
}
