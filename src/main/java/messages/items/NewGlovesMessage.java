package messages.items;

import client.appStates.ClientGameAppState;
import client.Main;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import game.items.armor.Gloves;
import lombok.Getter;
import server.ServerMain;

@Serializable
@Getter
public class NewGlovesMessage extends NewArmorMessage {

    public NewGlovesMessage() {
    }

    public NewGlovesMessage(Gloves item) {
        super(item);
    }

    @Override
    public void handleServer(ServerMain server,HostedConnection hc) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        Gloves i = (Gloves) ifa.createItem(id, getTemplate(), droppable);
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
