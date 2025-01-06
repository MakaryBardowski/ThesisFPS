package messages.messageListeners;

import client.Main;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import server.ServerMain;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import messages.TwoWayMessage;

@Serializable
public class ServerMessageListener implements MessageListener<HostedConnection> {

    private ServerMain serverApp;
    private static final Main MAIN_APP = Main.getInstance();

    public ServerMessageListener() {
    }

    public ServerMessageListener(ServerMain s) {
        this.serverApp = s;
    }

    @Override
    public void messageReceived(HostedConnection s, Message msg) {
        if (msg instanceof TwoWayMessage tm) {
            tm.handleServer(serverApp,s);
        }
    }


    public static void enqueueExecutionServer(Runnable runnable) {
        MAIN_APP.enqueue(runnable);
    }

}
