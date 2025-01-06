package messages;

import client.ClientGameAppState;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import server.ServerMain;

@Serializable
public class DeleteEntityMessage extends EntityUpdateMessage {

    public DeleteEntityMessage() {
    }

    public DeleteEntityMessage(int id) {
        super(id);
        setReliable(true);
    }

    @Override
    public void handleServer(ServerMain server,HostedConnection hc) {
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        var entity = getEntityByIdClient(id);
            if (entity != null) {
                entity.destroyClient();
            }
    }

}
