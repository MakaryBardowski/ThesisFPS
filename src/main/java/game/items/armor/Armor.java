package game.items.armor;

import game.items.Equippable;
import game.items.Item;
import com.jme3.scene.Node;
import game.items.ItemTemplates.ItemTemplate;
import lombok.Getter;
import lombok.Setter;

public abstract class Armor extends Item implements Equippable {
    @Getter
    @Setter
    protected float armorValue;

    public Armor(int id, ItemTemplate template,String name,Node node) {
        super(id,template,name,node);
    }

    public Armor(int id, ItemTemplate template,String name,Node node, boolean droppable) {
        super(id,template,name,node,droppable);
    }
}
