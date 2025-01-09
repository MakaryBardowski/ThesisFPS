package game.items;

import client.ClientGameAppState;
import com.jme3.network.AbstractMessage;
import com.jme3.scene.Node;
import game.entities.IntegerAttribute;
import game.entities.mobs.Mob;
import messages.items.MobItemInteractionMessage;
import messages.items.NewAmmoPackMessage;

public class AmmoPack extends Item {

    public static int AMMO_ATTRIBUTE = 0;
    public static int MAX_AMMO_ATTRIBUTE = 1;

    public AmmoPack(int id, ItemTemplates.ItemTemplate template, String name, Node node, short ammo, short maxAmmo) {
        super(id, template, name, node);
        attributes.put(AMMO_ATTRIBUTE, new IntegerAttribute(ammo));
        attributes.put(MAX_AMMO_ATTRIBUTE, new IntegerAttribute(maxAmmo));
    }

    public AmmoPack(int id, ItemTemplates.ItemTemplate template, String name, Node node, boolean droppable, short ammo, short maxAmmo) {
        super(id, template, name, node, droppable);
        attributes.put(AMMO_ATTRIBUTE, new IntegerAttribute(ammo));
        attributes.put(MAX_AMMO_ATTRIBUTE, new IntegerAttribute(maxAmmo));
    }

    @Override
    public void onInteract() {
        ClientGameAppState gs = ClientGameAppState.getInstance();
        MobItemInteractionMessage imsg = new MobItemInteractionMessage(this, gs.getPlayer(), MobItemInteractionMessage.ItemInteractionType.PICK_UP);
        imsg.setReliable(true);
        gs.getClient().send(imsg);
    }

    @Override
    public AbstractMessage createNewEntityMessage() {
        NewAmmoPackMessage msg = new NewAmmoPackMessage(this);
        msg.setReliable(true);
        return msg;
    }

    @Override
    public String getDescription() {
        StringBuilder builder = new StringBuilder();
        builder.append("-Medium size\n-Capacity [");
        builder.append(getAmmo());
        builder.append("/");
        builder.append(getMaxAmmo());
        builder.append("]");

        return builder.toString();
    }

    public int getMaxAmmo() {
        return ((IntegerAttribute) attributes.get(MAX_AMMO_ATTRIBUTE)).getValue();
    }

    public int getAmmo() {
        return ((IntegerAttribute) attributes.get(AMMO_ATTRIBUTE)).getValue();
    }

    public void setAmmo(int ammo) {
        ((IntegerAttribute) attributes.get(AMMO_ATTRIBUTE)).setValue(ammo);
    }
}
