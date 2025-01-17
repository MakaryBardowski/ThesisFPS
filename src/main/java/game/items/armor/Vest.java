package game.items.armor;

import client.ClientGameAppState;
import game.items.ItemTemplates;
import static game.map.blocks.VoxelLighting.setupModelLight;
import game.entities.mobs.player.Player;
import client.Main;
import com.jme3.network.AbstractMessage;
import com.jme3.scene.Node;
import static game.entities.DestructibleUtils.setupModelShootability;
import game.entities.mobs.HumanMob;
import game.entities.mobs.Mob;
import game.items.Holdable;
import game.items.ItemTemplates.VestTemplate;
import java.util.Arrays;
import messages.items.MobItemInteractionMessage;
import messages.items.NewHelmetMessage;
import messages.items.NewVestMessage;

public class Vest extends Armor {

    public Vest(int id, VestTemplate template, String name, Node node) {
        super(id, template, name, node);
        this.armorValue = template.getDefaultStats().getArmorValue();
    }

    public Vest(int id, VestTemplate template, String name, Node node, boolean droppable) {
        super(id, template, name, node, droppable);
        this.armorValue = template.getDefaultStats().getArmorValue();
    }

    @Override
    public void humanMobEquip(HumanMob m) {
        m.setVest(this);
        Node n = m.getSkinningControl().getAttachmentsNode("Spine");
        n.detachAllChildren();
        Node vest = (Node) Main.getInstance().getAssetManager().loadModel(template.getFpPath());
        setupModelLight(vest);
        n.attachChild(vest);
        setupModelShootability(vest, m.getId());
    }

    @Override
    public void humanMobUnequip(HumanMob m) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void playerEquip(Player p) {
        Vest unequippedItem = p.getVest();
        if (unequippedItem != null) {
            unequippedItem.playerUnequip(p);
        }
        humanMobEquip(p);
    }

    @Override
    public void playerUnequip(Player p) {
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
        NewVestMessage msg = new NewVestMessage(this);
        msg.setReliable(true);
        return msg;
    }

    @Override
    public void playerServerEquip(HumanMob m) {
        m.setVest(this);
    }

    @Override
    public void playerServerUnequip(HumanMob m) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public String getDescription() {
        StringBuilder builder = new StringBuilder();
        builder.append("-Worn\n");
        builder.append("Armor value: ");
        builder.append(armorValue);
        return builder.toString();
    }

}
