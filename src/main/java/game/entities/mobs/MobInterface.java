package game.entities.mobs;

import game.items.Item;

public interface MobInterface extends AiSteerable{

    
    public void attack();

    public void equip(Item e);

    public void unequip(Item e);

    public void equipServer(Item e);
    
    public void unequipServer(Item e);
    
    
}
