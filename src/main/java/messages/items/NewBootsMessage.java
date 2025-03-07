package messages.items;

import client.appStates.ClientGameAppState;
import client.Main;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import game.items.armor.Boots;
import lombok.Getter;
import server.ServerGameAppState;

@Serializable
@Getter
public class NewBootsMessage extends NewArmorMessage {

    public NewBootsMessage() {
    }

    public NewBootsMessage(Boots item) {
        super(item);
    }

    @Override
    public void handleServer(ServerGameAppState server, HostedConnection hc) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        Boots i = (Boots) ifa.createItem(id, getTemplate(), droppable);
        i.setArmorValue(armorValue);
        i.setName(name);
        if(isAlreadyDropped()){
            Main.getInstance().enqueue(()-> {
                i.drop(getDroppedPosition());
            });
        }
        client.registerEntity(i);
    }

}
