package messages;

import client.appStates.ClientGameAppState;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import game.entities.Entity;
import lombok.Getter;
import server.ServerGameAppState;

import java.util.Map;

@Serializable
public class BatchSetIntegerAttributeMessage extends TwoWayMessage {

    @Getter
    private int entityId;

    @Getter
    private Map<Integer,Integer> attributesByAttributeId;

    public BatchSetIntegerAttributeMessage() {
        setReliable(true);
    }

    public BatchSetIntegerAttributeMessage(Entity entity, Map<Integer,Integer> attributesByAttributeId) {
        this.entityId = entity.getId();
        this.attributesByAttributeId = attributesByAttributeId;
        setReliable(true);
    }

    @Override
    public void handleServer(ServerGameAppState server, HostedConnection hc) {
        var entity = server.getLevelManagerMobs().get(entityId);
        entity.setIntegerAttributesAndNotifyClients(attributesByAttributeId);
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        var entity = client.getMobs().get(entityId);
        entity.setIntegerAttributes(attributesByAttributeId);
    }

}
