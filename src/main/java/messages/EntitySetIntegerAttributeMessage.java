package messages;

import client.appStates.ClientGameAppState;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import game.entities.IntegerAttribute;
import game.entities.Entity;
import lombok.Getter;
import server.ServerMain;

@Serializable
public class EntitySetIntegerAttributeMessage extends TwoWayMessage {

    @Getter
    private int entityId;
    @Getter
    private int attributeId;
    @Getter
    private int attributeValue;

    public EntitySetIntegerAttributeMessage() {
        setReliable(true);
    }

    public EntitySetIntegerAttributeMessage(Entity entity, int attributeId, int attributeValue) {
        this.entityId = entity.getId();
        this.attributeId = attributeId;
        this.attributeValue = attributeValue;
        setReliable(true);
    }

    @Override
    public void handleServer(ServerMain server,HostedConnection hc) {
        var entity = server.getLevelManagerMobs().get(entityId);
        entity.setIntegerAttributeAndNotifyClients(attributeId, attributeValue);
        server.getServer().broadcast(this);

    }

    @Override
    public void handleClient(ClientGameAppState client) {
        var entity = client.getMobs().get(entityId);
        entity.setIntegerAttribute(attributeId, attributeValue);
        entity.attributeChangedNotification(attributeId, new IntegerAttribute(attributeValue));
    }

}
