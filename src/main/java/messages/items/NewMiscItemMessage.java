package messages.items;

import client.appStates.ClientGameAppState;
import client.Main;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import game.items.Item;
import server.ServerGameAppState;

@Serializable
public class NewMiscItemMessage extends NewItemMessage{

    public NewMiscItemMessage() {
    }

    public NewMiscItemMessage(Item item) {
        super(item);
    }

    @Override
    public void handleServer(ServerGameAppState server, HostedConnection sender) {
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
