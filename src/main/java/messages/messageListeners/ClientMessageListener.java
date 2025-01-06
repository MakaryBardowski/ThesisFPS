package messages.messageListeners;

import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import client.ClientGameAppState;
import messages.TwoWayMessage;
import messages.items.NewItemMessage;

public class ClientMessageListener implements MessageListener<Client> {

    private final ClientGameAppState clientApp;

    public ClientMessageListener(ClientGameAppState c) {
        this.clientApp = c;
    }

    @Override
    public void messageReceived(Client s, Message m) {
        if(m instanceof NewItemMessage nim) {
            System.out.println("received item with id: " + nim.getId() + " class "+nim.getClass());
        }
      if (m instanceof TwoWayMessage tm) {
            tm.handleClient(clientApp);
        }
    }
}
