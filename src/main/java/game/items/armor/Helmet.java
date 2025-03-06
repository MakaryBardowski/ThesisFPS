package game.items.armor;

import client.appStates.ClientGameAppState;

import static game.map.blocks.VoxelLighting.setupModelLight;
import game.entities.mobs.player.Player;
import client.Main;
import com.jme3.network.AbstractMessage;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import static game.entities.DestructibleUtils.setupModelShootability;
import game.entities.mobs.HumanMob;
import game.items.ItemTemplates.HelmetTemplate;
import messages.items.MobItemInteractionMessage;
import messages.items.NewHelmetMessage;

public class Helmet extends Armor {

    private static final float HELMET_SCALE_ZBUFFER_FIGHTING = 1.02f;

    public Helmet(int id, HelmetTemplate template, String name, Node node) {
        super(id, template, name, node);
        armorValue = template.getDefaultStats().getArmorValue();
    }

    public Helmet(int id, HelmetTemplate template, String name, Node node, boolean droppable) {
        super(id, template, name, node, droppable);
        armorValue = template.getDefaultStats().getArmorValue();
    }

    @Override
    public void humanMobEquipClient(HumanMob m) {
//        Node bb = m.getSkinningControl().getAttachmentsNode("BackpackBone");
//        Node ba = (Node) Main.getInstance().getAssetManager().loadModel("Models/backpack/backpack.j3o");
//        bb.attachChild(ba);
//        setupModelLight(ba);
//        setupModelShootability(ba, m.getId());
//        ba.move(0, 0.3f, 0);

        var helmetTemplate = (HelmetTemplate) template;
        m.setHelmet(this);
        Node n = m.getSkinningControl().getAttachmentsNode("Head");
        n.detachAllChildren();

//        m.getSkinningControl().getArmature().getJoint("Head").setLocalRotation(new Quaternion(1,0.6f,new Random().nextFloat(),1f));
        Node helmet = (Node) Main.getInstance().getAssetManager().loadModel(helmetTemplate.getFpPath());
        setupModelLight(helmet);
        n.attachChild(helmet);
        setupModelShootability(helmet, m.getId());

        if (!helmetTemplate.isReplacesHead()) {
            helmet.setLocalScale(HELMET_SCALE_ZBUFFER_FIGHTING);
            Node head = (Node) Main.getInstance().getAssetManager().loadModel(m.getDefaultHelmet().getTemplate().getFpPath());
            setupModelLight(head);
            n.attachChild(head);
            setupModelShootability(head, m.getId());
            ((Geometry) head.getChild(0)).getMaterial().getAdditionalRenderState().setPolyOffset(5, 5);
        }
    }

    @Override
    public void humanMobUnequipClient(HumanMob m) {
        m.getDefaultHelmet().humanMobEquipClient(m);
    }

    @Override
    public void playerEquipClient(Player m) {
        var unequippedItem = m.getHelmet();
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
        if (p.getHelmet() != this) {
            return;
        }

        humanMobUnequipClient(p);
        p.getDefaultHelmet().playerEquipClient(p);
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
        NewHelmetMessage msg = new NewHelmetMessage(this);
        msg.setReliable(true);
        return msg;
    }

    @Override
    public void serverEquip(HumanMob m) {
        m.setHelmet(this);
    }

    @Override
    public void serverUnequip(HumanMob m) {
        if(m.getHelmet() == this) {
            m.setHelmet(m.getDefaultHelmet());
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
