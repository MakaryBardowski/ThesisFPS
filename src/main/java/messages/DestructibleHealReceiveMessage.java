package messages;

import client.appStates.ClientGameAppState;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import game.entities.Destructible;
import lombok.Getter;
import server.ServerMain;

@Serializable
@Getter
public class DestructibleHealReceiveMessage extends EntityUpdateMessage {

    protected float heal;

    public DestructibleHealReceiveMessage() {
    }

    public DestructibleHealReceiveMessage(int targetId, float heal) {
        super(targetId);
        this.heal = heal;
    }

    @Override
    public void handleServer(ServerMain server,HostedConnection hc) {
        throw new UnsupportedOperationException("not implemented yet.");
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        enqueueExecution(() -> {
            if (entityExistsLocallyClient(id)) {

                Destructible d = (Destructible) getEntityByIdClient(id);
                d.receiveHealClient(heal);
            }
        }
        );
    }

    private void applyDestructibleHealAndNotifyClients(Destructible d, ServerMain serverApp) {
        d.setHealth(d.getHealth() + heal);
        this.setReliable(true);
        serverApp.getServer().broadcast(this);
    }

    public void handleDestructibleHealReceive(Destructible d, ServerMain serverApp) {
        applyDestructibleHealAndNotifyClients(d, serverApp);
    }

}
