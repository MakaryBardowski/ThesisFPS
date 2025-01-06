package game.items.weapons;

import game.entities.mobs.Mob;
import game.entities.mobs.player.Player;

public interface Attacks {
    void attack(Mob m);
    void playerAttack(Player p);
}
