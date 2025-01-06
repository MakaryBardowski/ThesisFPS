package game.items;

import game.entities.mobs.HumanMob;
import game.entities.mobs.player.Player;

public interface Equippable {

    public void playerEquip(Player m);

    public void playerUnequip(Player m);

    public void playerServerEquip(HumanMob m);

    public void playerServerUnequip(HumanMob m);

    public void humanMobEquip(HumanMob m);

    public void humanMobUnequip(HumanMob m);

}
