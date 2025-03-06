package messages.messageListeners;

import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import client.appStates.ClientGameAppState;
import messages.TwoWayMessage;

public class ClientMessageListener implements MessageListener<Client> {

    private final ClientGameAppState clientApp;

    public ClientMessageListener(ClientGameAppState c) {
        this.clientApp = c;
    }

    @Override
    public void messageReceived(Client s, Message m) {
      if (m instanceof TwoWayMessage tm) {
            tm.handleClient(clientApp);
        }
    }
}
