package messages.items;

import client.appStates.ClientGameAppState;
import client.Main;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import game.items.armor.Helmet;
import lombok.Getter;
import server.ServerMain;

@Serializable
@Getter
public class NewHelmetMessage extends NewArmorMessage {

    public NewHelmetMessage() {
    }

    public NewHelmetMessage(Helmet item) {
        super(item);
    }

    @Override
    public void handleServer(ServerMain server,HostedConnection hc) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        Helmet i = (Helmet) ifa.createItem(id, getTemplate(), droppable);
        i.setArmorValue(armorValue); // so we set the params
        i.setName(name);
        if(isAlreadyDropped()){
            Main.getInstance().enqueue(()-> {
                i.drop(getDroppedPosition());
            });
        }
        client.registerEntity(i);
    }

}
