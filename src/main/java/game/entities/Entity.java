package game.entities;

import com.jme3.network.AbstractMessage;
import events.EventPublisher;
import game.entities.mobs.Mob;

import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;
import lombok.Setter;
import messages.DeleteEntityMessage;
import messages.EntitySetFloatAttributeMessage;
import messages.EntitySetIntegerAttributeMessage;
import server.ServerMain;

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
//        var dem = new DeleteEntityMessage(id);
        dem.setId(id);
        ServerMain.getInstance().getServer().broadcast(dem);
    }
    // this should be abstract!
    public void destroyClient(){};
    // this should be abstract!

    public void destroyServer(){};

    public abstract AbstractMessage createNewEntityMessage();

    public void attributeChangedNotification(int attributeId, Attribute copyOfAttribute) {}

    public void setFloatAttributeAndNotifyClients(int attributeId, float val) {
        setFloatAttribute(attributeId, val);
        var msg = new EntitySetFloatAttributeMessage(this, attributeId, val);
        ServerMain.getInstance().getServer().broadcast(msg);
    }

    public void setIntegerAttributeAndNotifyClients(int attributeId, int val) {
        setIntegerAttribute(attributeId, val);
        var msg = new EntitySetIntegerAttributeMessage(this, attributeId, val);
        ServerMain.getInstance().getServer().broadcast(msg);
    }

    public void setFloatAttribute(int attributeId, float val) {
        getFloatAttribute(attributeId).setValue(val);
    }

    public void setIntegerAttribute(int attributeId, int val) {
        getIntegerAttribute(attributeId).setValue(val);
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
