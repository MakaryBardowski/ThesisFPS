package messages.items;

import client.appStates.ClientGameAppState;
import client.Main;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import game.items.armor.Vest;
import lombok.Getter;
import server.ServerGameAppState;

@Serializable
@Getter
public class NewVestMessage extends NewArmorMessage {

    public NewVestMessage() {
    }

    public NewVestMessage(Vest item) {
        super(item);
    }

    @Override
    public void handleServer(ServerGameAppState server, HostedConnection hc) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        Vest i = (Vest) ifa.createItem(id, getTemplate(), droppable);
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
