package messages.items;

import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;
import game.items.Item;
import game.items.ItemTemplates;
import game.items.ItemTemplates.ItemTemplate;
import game.items.factories.ItemFactory;
import lombok.Getter;
import messages.TwoWayMessage;

@Serializable
@Getter
public abstract class NewItemMessage extends TwoWayMessage {
    public static final ItemFactory ifa = new ItemFactory();
    protected int id;
    protected int templateIndex;
    protected boolean droppable;

    // Todo: dropped item
    protected boolean alreadyDropped;
    protected float droppedX;
    protected float droppedY;
    protected float droppedZ;

    public NewItemMessage() {
    }

    public NewItemMessage(Item item) {
        this.id = item.getId();
        this.templateIndex = item.getTemplate().getTemplateIndex();
        this.droppable = item.isDroppable();

        var dropLocationOnServer = item.getNode().getWorldTranslation();

        this.alreadyDropped = item.isDroppedOnServer();
        this.droppedX = dropLocationOnServer.x;
        this.droppedY = dropLocationOnServer.y;
        this.droppedZ = dropLocationOnServer.z;
    }
    
    public ItemTemplate getTemplate(){
        return ItemTemplates.getTemplateByIndex(templateIndex);
    }

    public Vector3f getDroppedPosition(){
        return new Vector3f(droppedX,droppedY,droppedZ);
    }
}
