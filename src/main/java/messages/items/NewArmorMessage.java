package messages.items;

import com.jme3.network.serializing.Serializable;
import game.items.armor.Armor;
import lombok.Getter;

@Serializable
@Getter
public abstract class NewArmorMessage extends NewItemMessage {

    protected float armorValue;

    public NewArmorMessage() {
    }

    public NewArmorMessage(Armor item) {
        super(item);
        this.armorValue = item.getArmorValue();
    }

}
