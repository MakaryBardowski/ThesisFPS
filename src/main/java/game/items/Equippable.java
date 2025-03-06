package game.items;

import game.entities.mobs.HumanMob;
import game.entities.mobs.player.Player;

public interface Equippable {

    public void playerEquipClient(Player m);

    public void playerUnequipClient(Player m);

    public void serverEquip(HumanMob m);

    public void serverUnequip(HumanMob m);

    public void humanMobEquipClient(HumanMob m);

    public void humanMobUnequipClient(HumanMob m);

}
