package messages.items;

import client.appStates.ClientGameAppState;
import client.Main;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import game.items.AmmoPack;
import lombok.Getter;
import server.ServerMain;

@Serializable
@Getter
public class NewAmmoPackMessage extends NewItemMessage {

    private int ammo;

    public NewAmmoPackMessage() {
    }

    public NewAmmoPackMessage(AmmoPack item) {
        super(item);
        this.ammo = item.getAmmo();
    }

    @Override
    public void handleServer(ServerMain server,HostedConnection hc) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        AmmoPack i = (AmmoPack) ifa.createItem(id, getTemplate(), droppable);
        i.setAmmo(ammo);
        if(isAlreadyDropped()){
            Main.getInstance().enqueue(()-> {
                i.drop(getDroppedPosition());
            });
        }
        client.registerEntity(i);
    }

}
