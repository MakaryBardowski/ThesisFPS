package messages.items;

import client.appStates.ClientGameAppState;
import client.Main;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import game.entities.IntegerAttribute;
import game.items.weapons.RangedWeapon;
import lombok.Getter;
import server.ServerMain;

@Serializable
@Getter
public class NewRangedWeaponMessage extends NewItemMessage {
    private int ammo;

    public NewRangedWeaponMessage() {
    }

    public NewRangedWeaponMessage(RangedWeapon item) {
        super(item);
        this.ammo = ((IntegerAttribute)item.getAttributes().get(RangedWeapon.AMMO_ATTRIBUTE)).getValue();
    }

    @Override
    public void handleServer(ServerMain server,HostedConnection hc) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void handleClient(ClientGameAppState client) {
            RangedWeapon i = (RangedWeapon) ifa.createItem(id, getTemplate(), droppable);
            i.setAmmo(ammo);
            if(isAlreadyDropped()){
                Main.getInstance().enqueue(()-> {
                    i.drop(getDroppedPosition());
                });
            }
            client.registerEntity(i);
    }

}
