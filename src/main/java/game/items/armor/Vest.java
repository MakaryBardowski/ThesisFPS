package game.items.armor;

import client.appStates.ClientGameAppState;

import static game.map.blocks.VoxelLighting.setupModelLight;
import game.entities.mobs.player.Player;
import client.Main;
import com.jme3.network.AbstractMessage;
import com.jme3.scene.Node;
import static game.entities.DestructibleUtils.setupModelShootability;
import game.entities.mobs.HumanMob;
import game.items.ItemTemplates.VestTemplate;
import messages.items.MobItemInteractionMessage;
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
    public void humanMobEquipClient(HumanMob m) {
        m.setVest(this);
        Node n = m.getSkinningControl().getAttachmentsNode("Spine");
        n.detachAllChildren();
        Node vest = (Node) Main.getInstance().getAssetManager().loadModel(template.getFpPath());
        setupModelLight(vest);
        n.attachChild(vest);
        setupModelShootability(vest, m.getId());
    }

    @Override
    public void humanMobUnequipClient(HumanMob m) {
        m.getDefaultVest().humanMobEquipClient(m);
    }

    @Override
    public void playerEquipClient(Player p) {
        var unequippedItem = p.getVest();
        if(unequippedItem == this){
            return;
        }
        if (unequippedItem != null) {
            unequippedItem.playerUnequipClient(p);
        }
        humanMobEquipClient(p);
    }

    @Override
    public void playerUnequipClient(Player p) {
        if (p.getVest() != this) {
            return;
        }

        humanMobUnequipClient(p);
        p.getDefaultVest().playerEquipClient(p);
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
    public void serverEquip(HumanMob m) {
        m.setVest(this);
    }

    @Override
    public void serverUnequip(HumanMob m) {
        if(m.getVest() == this) {
            m.setVest(m.getDefaultVest());
        }
    }

    @Override
    public String getDescription() {
        StringBuilder builder = new StringBuilder();
        builder.append("Armor value: ");
        builder.append(armorValue);
        return builder.toString();
    }

}
