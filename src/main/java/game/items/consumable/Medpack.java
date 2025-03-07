package game.items.consumable;

import game.items.weapons.*;
import client.appStates.ClientGameAppState;
import com.jme3.network.AbstractMessage;
import com.jme3.scene.Node;
import game.entities.mobs.HumanMob;
import game.entities.mobs.Mob;
import game.entities.mobs.player.Player;
import game.items.ItemTemplates;
import messages.items.MobItemInteractionMessage;
import messages.items.NewGrenadeMessage;
import com.jme3.network.Filters;
import messages.DestructibleHealReceiveMessage;
import server.ServerGameAppState;
import static server.ServerGameAppState.removeEntityByIdServer;

public class Medpack extends ThrowableWeapon {

    private float healing = 10;

    public Medpack(int id, float damage, ItemTemplates.ItemTemplate template, String name, Node node) {
        super(id, damage, template, name, node);
    }

    public Medpack(int id, float damage, ItemTemplates.ItemTemplate template, String name, Node node, boolean droppable) {
        super(id, damage, template, name, node, droppable);
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
        NewGrenadeMessage msg = new NewGrenadeMessage(this);
        msg.setReliable(true);
        return msg;
    }

    @Override
    public void attack(Mob m) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void playerAttack(Player p) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void playerHoldInRightHand(Player p) {
//        AssetManager assetManager = Main.getInstance().getAssetManager();
//
//        p.setEquippedRightHand(this);
//
//        if (playerIsMyPlayer(p)) {
//            ClientGameAppState.getInstance().getNifty().getCurrentScreen().findControl("ammo", LabelControl.class).setText("");
//
//            Node model = (Node) assetManager.loadModel(template.getFpPath());
//
//            Geometry ge = (Geometry) model.getChild(0);
//            Material originalMaterial = ge.getMaterial();
//            Material newMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
//            newMaterial.setTexture("DiffuseMap", originalMaterial.getTextureParam("BaseColorMap").getTextureValue());
//            ge.setMaterial(newMaterial);
//
//            p.getFirstPersonHands().attachToHandR(model);
//            model.scale(2.f);
//            model.move(0.1f, 0.2f, -0.4f);
////            model.move(new Vector3f(0.43199998f, 0.46799996f, -1.6199999f));
//
//            ((ClipAction) p.getFirstPersonHands().getHandsComposer().action("ThrowGrenade")).setTransitionLength(0);
//
//            p.getFirstPersonHands().setHandsAnim(FirstPersonHandAnimationData.HOLD_GRENADE);
//
//            p.getFirstPersonHands().getHandsComposer().setGlobalSpeed(1f);
//        }
//
//        Node model = (Node) assetManager.loadModel(template.getDropPath());
//        model.move(0, -0.33f, 0.2f);
//
//        Geometry ge = (Geometry) (model.getChild(0));
//        Material originalMaterial = ge.getMaterial();
//        Material newMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
//        newMaterial.setTexture("DiffuseMap", originalMaterial.getTextureParam("BaseColorMap").getTextureValue());
//        ge.setMaterial(newMaterial);
//        float length = 1f;
//        float width = 1f;
//        float height = 1f;
//        model.scale(length, width, height);
//        p.getSkinningControl().getAttachmentsNode("HandR").attachChild(model);
//        setupModelShootability(model, p.getId());
//        thirdPersonModelParentIndex = p.getSkinningControl().getAttachmentsNode("HandR").getChildIndex(model);
//        System.out.println("name " + p.getName());
//        System.out.println(" EQUIPPED A GRENADE! (pos = " + model.getWorldTranslation());

    }

    @Override
    public void playerUseInRightHand(Player p) {
//        var composer = p.getFirstPersonHands().getHandsComposer();
//
//        Action toIdle = composer.action("KnifeAttackToHold");
//        Tween idle = Tweens.callMethod(composer, "setCurrentAction", "HoldKnife");
//        composer.actionSequence("ThrowToHold", toIdle, idle);
//
//        Action attack = composer.action("ThrowGrenade");
//        Tween attackToIdle = Tweens.callMethod(composer, "setCurrentAction", "ThrowToHold");
//        composer.actionSequence("fullSwing", attack, attackToIdle);
//
//        ((ClipAction) composer.action("ThrowGrenade")).setTransitionLength(0);
//        composer.setCurrentAction("fullSwing");
//        composer.getCurrentAction().setSpeed(1.5f);
//        throwGrenade(p);
    }

    @Override
    public void playerEquipClient(Player p) {

    }

    @Override
    public void playerUnequipClient(Player p) {
        if (p.getEquippedRightHand() != this) {
            return;
        }
//            p.setEquippedRightHand(null);
//            p.getFirstPersonHands().getRightHandEquipped().detachAllChildren();
//            p.getSkinningControl().getAttachmentsNode("HandR").detachChildAt(thirdPersonModelParentIndex);
//            System.out.println("unequipping GRENADE!");
//        
    }

    @Override
    public void serverEquip(HumanMob m) {
        var sm = ServerGameAppState.getInstance();
        var hc = sm.getHostsByPlayerId().get(m.getId());
        Filters.notEqualTo(hc);

        var heal = Math.min(healing, m.getMaxHealth() - m.getHealth());
        m.setHealth(m.getHealth() + heal);

        var msg = new DestructibleHealReceiveMessage(m.getId(), heal);
        msg.setReliable(true);
        sm.getServer().broadcast(msg);

        ServerGameAppState.removeItemFromMobEquipmentServer(m.getId(), id);
        removeEntityByIdServer(id);

        MobItemInteractionMessage imsg = new MobItemInteractionMessage(id, m.getId(), MobItemInteractionMessage.ItemInteractionType.DESTROY);
        imsg.setReliable(true);
        sm.getServer().broadcast(imsg);
    }

    @Override
    public void serverUnequip(HumanMob m) {
//        m.setEquippedRightHand(null);
    }

    private boolean playerIsMyPlayer(Player p) {
        return p == ClientGameAppState.getInstance().getPlayer();
    }

    @Override
    public String getDescription() {
        StringBuilder builder = new StringBuilder();
        builder.append("-Restores \\#32cd3010#");
        builder.append(healing);
        builder.append(" Health");
        builder.append("\\#8a8a3d# on use\n");
        return builder.toString();
    }
}
