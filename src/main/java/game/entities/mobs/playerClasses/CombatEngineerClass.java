package game.entities.mobs.playerClasses;

import game.items.ItemTemplates;
import static game.items.ItemTemplates.*;
import java.util.List;

public class CombatEngineerClass extends PlayerClass{

    @Override
    public String getDescription() {
        return "In the war-torn landscapes of The Great War, the Combat Engineer stands as a "
                + "master of destruction and construction. Armed with a versatile SMG and equipped"
                + " with explosive charges, engineers excel at demolishing enemy fortifications "
                + "and creating vital battlefield structures. From breaching obstacles with explosives to "
                + "deploying essential barriers, the Combat Engineer is a strategic force, shaping "
                + "the ebb and flow of the war with ingenuity and raw firepower.";
    }

 @Override
    public List<ItemTemplates.ItemTemplate> getStartingEquipmentTemplates() {
        return List.of(
                AXE,
                PISTOL_C96,
                TRENCH_HELMET,
                VEST_TRENCH,
                BOOTS_TRENCH
        );

    }


    
}
