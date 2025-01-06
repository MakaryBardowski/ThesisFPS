package game.entities.mobs.playerClasses;

import game.items.ItemTemplates;
import java.util.List;

public abstract class PlayerClass {
    
    
    
    public static final PlayerClass getClassByIndex(int index){
    if(index == 0)
        return new AssaultClass();
    if(index == 1)
        return new MedicClass();
    if(index == 2)
        return new CombatEngineerClass();
    return null;
    }
    
    public abstract String getDescription();
    public abstract List<ItemTemplates.ItemTemplate> getStartingEquipmentTemplates();
}
