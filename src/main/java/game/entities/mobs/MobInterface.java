package game.entities.mobs;

import game.items.Item;

public interface MobInterface extends AiSteerable{

    
    void attack();

    void equip(Item e);

    void unequip(Item e);

    void equipServer(Item e);
    
    void unequipServer(Item e);
    
    
}
