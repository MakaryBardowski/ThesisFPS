package messages;

import client.appStates.ClientGameAppState;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import game.entities.Entity;
import lombok.Getter;
import server.ServerGameAppState;

@Serializable
public class EntitySetFloatAttributeMessage extends TwoWayMessage {

    @Getter
    private int entityId;
    @Getter
    private int attributeId;
    @Getter
    private float attributeValue;

    public EntitySetFloatAttributeMessage() {
        setReliable(true);
    }

    public EntitySetFloatAttributeMessage(Entity entity, int attributeId, float attributeValue) {
        this.entityId = entity.getId();
        this.attributeId = attributeId;
        this.attributeValue = attributeValue;
        setReliable(true);
    }

    @Override
    public void handleServer(ServerGameAppState server, HostedConnection hc) {
        var entity = server.getLevelManagerMobs().get(entityId);
        entity.setFloatAttributeAndNotifyClients(attributeId, attributeValue);
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        var entity = client.getMobs().get(entityId);
        entity.setFloatAttribute(attributeId, attributeValue);
    }

}
