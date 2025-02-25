package game.entities;

import com.jme3.network.AbstractMessage;
import events.EventPublisher;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;
import lombok.Setter;
import messages.*;
import server.ServerGameAppState;

@Getter
public abstract class Entity extends EventPublisher {
    private static final DeleteEntityMessage dem = new DeleteEntityMessage();

    @Getter
    protected ConcurrentHashMap<Integer, Attribute> attributes = new ConcurrentHashMap<>(0);

    protected int id;
    @Setter
    protected String name;

    public Entity(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public final void destroyOnServerAndNotify() {
        destroyServer();
        dem.setId(id);
        ServerGameAppState.getInstance().getServer().broadcast(dem);
    }
    // this should be abstract!
    public void destroyClient(){};
    // this should be abstract!

    public void destroyServer(){};

    public abstract AbstractMessage createNewEntityMessage();

    public void attributeChangedNotification(int attributeId, Attribute oldAttributeCopy, Attribute copyOfNewAttribute) {}

    public void setFloatAttributeAndNotifyClients(int attributeId, float val) {
        setFloatAttribute(attributeId, val);
        var msg = new EntitySetFloatAttributeMessage(this, attributeId, val);
        ServerGameAppState.getInstance().getServer().broadcast(msg);
    }

    public void setIntegerAttributeAndNotifyClients(int attributeId, int val) {
        setIntegerAttribute(attributeId, val);
        var msg = new EntitySetIntegerAttributeMessage(this, attributeId, val);
        ServerGameAppState.getInstance().getServer().broadcast(msg);
    }

    public void setFloatAttributesAndNotifyClients(Map<Integer,Float> floatAttributesById) {
        setFloatAttributes(floatAttributesById);
        var msg = new BatchSetFloatAttributeMessage(this, floatAttributesById);
        ServerGameAppState.getInstance().getServer().broadcast(msg);
    }

    public void setIntegerAttributesAndNotifyClients(Map<Integer,Integer> integerAttributesById) {
        setIntegerAttributes(integerAttributesById);
        var msg = new BatchSetIntegerAttributeMessage(this, integerAttributesById);
        ServerGameAppState.getInstance().getServer().broadcast(msg);
    }

    public void setFloatAttribute(int attributeId, float val) {
        var oldValue = getFloatAttribute(attributeId).getValue();
        getFloatAttribute(attributeId).setValue(val);
        attributeChangedNotification(attributeId,new FloatAttribute(oldValue),new FloatAttribute(val));
    }

    public void setIntegerAttribute(int attributeId, int val) {
        var oldValue = getIntegerAttribute(attributeId).getValue();
        getIntegerAttribute(attributeId).setValue(val);
        attributeChangedNotification(attributeId, new IntegerAttribute(oldValue),new IntegerAttribute(val));
    }

    public void setFloatAttributes(Map<Integer,Float> integerAttributesById) {
        for(var entry : integerAttributesById.entrySet() ){
            setFloatAttribute(entry.getKey(), entry.getValue());
        }
    }

    public void setIntegerAttributes(Map<Integer,Integer> integerAttributesById) {
        for(var entry : integerAttributesById.entrySet() ){
            setIntegerAttribute(entry.getKey(), entry.getValue());
        }
    }

    public FloatAttribute getFloatAttribute(int attributeId) {
        return ((FloatAttribute) attributes.get(attributeId));
    }

    public IntegerAttribute getIntegerAttribute(int attributeId) {
        return ((IntegerAttribute) attributes.get(attributeId));
    }

    @Override
    public int hashCode() {
        return id;
    }
    
}
