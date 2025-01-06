package messages;

import com.jme3.network.serializing.Serializable;
import lombok.Getter;
import lombok.Setter;

@Serializable
public abstract class EntityUpdateMessage extends TwoWayMessage {
@Setter
    @Getter
    protected int id;

    public EntityUpdateMessage() {
    }

    public EntityUpdateMessage(int id) {
        this.id = id;
    }

    

}
