package game.items.armor;

import client.appStates.ClientGameAppState;
import static game.map.blocks.VoxelLighting.setupModelLight;
import game.entities.mobs.player.Player;
import client.Main;
import com.jme3.math.FastMath;
import com.jme3.network.AbstractMessage;
import com.jme3.scene.Node;
import static game.entities.DestructibleUtils.setupModelShootability;
import game.entities.mobs.HumanMob;
import game.items.ItemTemplates.BootsTemplate;
import messages.items.MobItemInteractionMessage;
import messages.items.NewBootsMessage;

public class Boots extends Armor {

    public Boots(int id, BootsTemplate template, String name, Node node) {
        super(id, template, name, node);
        this.armorValue = template.getDefaultStats().getArmorValue();
    }

    public Boots(int id, BootsTemplate template, String name, Node node, boolean droppable) {
        super(id, template, name, node, droppable);
        this.armorValue = template.getDefaultStats().getArmorValue();
    }

    @Override
    public void humanMobEquipClient(HumanMob m) {
        var verticalOffset = 0.44f;
        m.setBoots(this);
        Node r = m.getSkinningControl().getAttachmentsNode("LegR");
        Node l = m.getSkinningControl().getAttachmentsNode("LegL");
        r.detachAllChildren();
        l.detachAllChildren();

        Node bootR = (Node) Main.getInstance().getAssetManager().loadModel(template.getFpPath().replace("?", "R"));
        setupModelLight(bootR);
        setupModelShootability(bootR, m.getId());

        bootR.move(0, verticalOffset, 0);

        r.attachChild(bootR);

        Node bootL = (Node) Main.getInstance().getAssetManager().loadModel(template.getFpPath().replace("?", "L"));
        setupModelLight(bootL);
        setupModelShootability(bootL, m.getId());
        bootL.move(0, verticalOffset, 0);

        l.attachChild(bootL);
        bootR.rotate(0, -FastMath.DEG_TO_RAD * 90, 0);
        bootL.rotate(0, -FastMath.DEG_TO_RAD * 90, 0);
    }

    @Override
    public void humanMobUnequipClient(HumanMob m) {
        m.getDefaultBoots().humanMobEquipClient(m);
    }

    @Override
    public void playerEquipClient(Player m) {
        var unequippedItem = m.getBoots();
        if(unequippedItem == this){
            return;
        }
        if (unequippedItem != null) {
            unequippedItem.playerUnequipClient(m);
        }
        humanMobEquipClient(m);
    }

    @Override
    public void playerUnequipClient(Player p) {
        if (p.getBoots() != this) {
            return;
        }

        humanMobUnequipClient(p);
        p.getDefaultBoots().playerEquipClient(p);
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
        NewBootsMessage msg = new NewBootsMessage(this);
        msg.setReliable(true);
        return msg;
    }

    @Override
    public void serverEquip(HumanMob m) {
        m.setBoots(this);
    }

    @Override
    public void serverUnequip(HumanMob m) {
        if(m.getBoots() == this) {
            m.setBoots(m.getBoots());
        }
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
