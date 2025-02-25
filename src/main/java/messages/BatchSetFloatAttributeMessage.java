package messages;

import client.appStates.ClientGameAppState;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import game.entities.Entity;
import lombok.Getter;
import server.ServerGameAppState;

import java.util.Map;

@Serializable
public class BatchSetFloatAttributeMessage extends TwoWayMessage {

    @Getter
    private int entityId;

    @Getter
    private Map<Integer,Float> attributesByAttributeId;

    public BatchSetFloatAttributeMessage() {
        setReliable(true);
    }

    public BatchSetFloatAttributeMessage(Entity entity, Map<Integer,Float> attributesByAttributeId) {
        this.entityId = entity.getId();
        this.attributesByAttributeId = attributesByAttributeId;
        setReliable(true);
    }

    @Override
    public void handleServer(ServerGameAppState server, HostedConnection hc) {
        var entity = server.getLevelManagerMobs().get(entityId);
        entity.setFloatAttributesAndNotifyClients(attributesByAttributeId);
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        var entity = client.getMobs().get(entityId);
        entity.setFloatAttributes(attributesByAttributeId);
    }

}
