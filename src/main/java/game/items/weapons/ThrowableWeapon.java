package game.items.weapons;

import com.jme3.scene.Node;
import game.items.ItemTemplates;

public abstract class ThrowableWeapon extends Weapon{
    
    public ThrowableWeapon(int id,float damage, ItemTemplates.ItemTemplate template,String name,Node node) {
        super(id,damage,template,name,node,1);
    }

    public ThrowableWeapon(int id,float damage, ItemTemplates.ItemTemplate template,String name,Node node, boolean droppable) {
        super(id,damage,template,name,node,droppable,1);
    }
}
