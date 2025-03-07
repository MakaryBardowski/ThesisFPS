package game.entities.mobs;

import behaviorTree.BehaviorNode;
import behaviorTree.BehaviorTree;
import behaviorTree.LeafNode;
import behaviorTree.actions.simpleHumanActions.*;
import behaviorTree.composite.ParallelNode;
import behaviorTree.composite.SelectorNode;
import behaviorTree.composite.SequenceNode;
import behaviorTree.context.SimpleHumanMobContext;
import game.effects.EmitterPooler;
import game.entities.*;
import game.entities.factories.MobSpawnType;
import game.items.Equippable;
import game.items.Holdable;
import game.items.Item;
import game.map.collision.WorldGrid;
import client.appStates.ClientGameAppState;
import static client.appStates.ClientGameAppState.removeEntityByIdClient;
import client.ClientSynchronizationUtils;
import client.Main;
import com.jme3.anim.AnimComposer;
import com.jme3.anim.ArmatureMask;
import com.jme3.anim.SkinningControl;
import com.jme3.anim.tween.Tweens;
import com.jme3.effect.ParticleEmitter;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import data.DamageReceiveData;
import events.DamageReceivedEvent;
import game.effects.ParticleUtils;

import game.items.armor.Boots;
import game.items.armor.Gloves;
import game.items.armor.Helmet;
import game.items.armor.Vest;
import game.map.collision.RectangleAABB;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import lombok.Getter;
import lombok.Setter;
import messages.DestructibleDamageReceiveMessage;
import messages.NewMobMessage;
import server.ServerGameAppState;
import static server.ServerGameAppState.removeEntityByIdServer;

public class HumanMob extends Mob {
    private static final Random RANDOM = new Random();
    public static final String HUMAN_SKELETON_RIG_PATH = "Models/HumanSkeleton/HumanSkeleton.j3o";
    protected Holdable equippedRightHand;
    protected Holdable equippedLeftHand;
    protected SkinningControl skinningControl;

    @Getter
    protected final Node thirdPersonHandsNode;

    @Getter
    protected final Node armatureNode;

    @Getter
    @Setter
    protected Helmet helmet;
    @Getter
    @Setter
    protected Helmet defaultHelmet; // equipped when nothing is equipped (bare head)
    @Getter
    @Setter
    protected Vest vest;
    @Getter
    @Setter
    protected Vest defaultVest;
    @Getter
    @Setter
    protected Boots boots;
    @Getter
    @Setter
    protected Boots defaultBoots;
    @Getter
    @Setter
    protected Gloves gloves;
    @Getter
    @Setter
    protected Gloves defaultGloves;

    @Getter
    private final AnimComposer modelComposer;

    private Geometry hitboxDebug;

    public HumanMob(MobSpawnType mobSpawnType,int id, Node node, String name, SkinningControl skinningControl, AnimComposer modelComposer) {
        super(mobSpawnType,id, node, name);
        setHealth(24);
        setMaxHealth(24);
        armatureNode = (Node) node.getChild("Armature");
        thirdPersonHandsNode = new Node();
        armatureNode.attachChild(thirdPersonHandsNode); // attach it to the Node holding the base mesh
        
        this.skinningControl = skinningControl;
        this.modelComposer = modelComposer;

        var armature = skinningControl.getArmature();
        ArmatureMask legsMask = new ArmatureMask();
        legsMask.addBones(armature, "LegL");
        legsMask.addBones(armature, "LegR");
        legsMask.addBones(armature, "Spine");
        modelComposer.makeLayer("Legs", legsMask);

        ArmatureMask handsMask = new ArmatureMask();
        handsMask.addBones(armature, "HandR");
        handsMask.addBones(armature, "HandL");

        modelComposer.makeLayer("Hands", handsMask);
        modelComposer.getLayer("Hands").setLooping(false);

//        if(new Random().nextFloat() < 0.5f){
//        modelComposer.setCurrentAction("AttackSwipe1H", "Hands");
//        modelComposer.getCurrentAction("Hands").setSpeed(0.2f);
//        }
        modelComposer.setCurrentAction("Idle", "Legs");

        createHitbox();
//        node.attachChild(Circle.createCircle(20, ColorRGBA.Red));

        cachedSpeed = 7.5f;
        attributes.put(SPEED_ATTRIBUTE_KEY, new FloatAttribute(cachedSpeed));
        onInteract();

//        debugSkeleton(node);
    }

    @Override
    public void onInteract() {
//        if (ServerMain.getInstance() == null) {
//            return;
//        }
//        if (node.getControl(HumanPathDebugControl.class) == null) {
//            node.addControl(new HumanPathDebugControl(this));
//        }
//        System.out.println(Arrays.toString(equipment));
//
//        System.out.println("helmet: " + helmet);
//        System.out.println("Armor: " + vest);
//        System.out.println("Gloves: " + gloves);
//        System.out.println("boots: " + boots);

        System.out.println(name + " says hi! Im being debugged!");
    }

    @Override
    public void moveClient(float tpf) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void die() {
        dropEquipment();
        List<Node> guts = new ArrayList<>();
        Node gore = (Node) Main.getInstance().getAssetManager().loadModel("Models/Gore/goreTorso0.j3o");
        ParticleUtils.spawnGorePhysicalParticleShaded(gore, node.getWorldTranslation().add(new Vector3f(0, 1.2f, 0)));
        Node gore1 = (Node) Main.getInstance().getAssetManager().loadModel("Models/Gore/goreTorso1.j3o");
        ParticleUtils.spawnGorePhysicalParticleShaded(gore1, node.getWorldTranslation().add(new Vector3f(0.1f, 1.4f, -0.1f)));
        Node gore2 = (Node) Main.getInstance().getAssetManager().loadModel("Models/Gore/goreTorso2.j3o");
        ParticleUtils.spawnGorePhysicalParticleShaded(gore2, node.getWorldTranslation().add(new Vector3f(-0.15f, 1.6f, 0.1f)));
        Node goreHand = (Node) Main.getInstance().getAssetManager().loadModel("Models/Gore/goreHand0.j3o");
        ParticleUtils.spawnGorePhysicalParticleShaded(goreHand, node.getWorldTranslation().add(new Vector3f(0.6f, 1.2f, 0)));
        Node goreHand1 = (Node) Main.getInstance().getAssetManager().loadModel("Models/Gore/goreHand1.j3o");
        ParticleUtils.spawnGorePhysicalParticleShaded(goreHand1, node.getWorldTranslation().add(new Vector3f(-0.6f, 1.2f, 0)));
        Node goreHand2 = (Node) Main.getInstance().getAssetManager().loadModel("Models/Gore/goreHand2.j3o");
        ParticleUtils.spawnGorePhysicalParticleShaded(goreHand2, node.getWorldTranslation().add(new Vector3f(-0.6f, 1.2f, 0)));
        Node goreHead = (Node) Main.getInstance().getAssetManager().loadModel("Models/Gore/goreHead0.j3o");
        ParticleUtils.spawnGorePhysicalParticleShaded(goreHead, node.getWorldTranslation().add(new Vector3f(0f, 2.12f, 0)));
        Node goreHead1 = (Node) Main.getInstance().getAssetManager().loadModel("Models/Gore/goreHead1.j3o");
        ParticleUtils.spawnGorePhysicalParticleShaded(goreHead1, node.getWorldTranslation().add(new Vector3f(0.1f, 2.2f, -0.1f)));
        Node goreLeg = (Node) Main.getInstance().getAssetManager().loadModel("Models/Gore/goreLeg0.j3o");
        ParticleUtils.spawnGorePhysicalParticleShaded(goreLeg, node.getWorldTranslation().add(new Vector3f(0.6f, 0.6f, 0.1f)));
        Node goreLeg1 = (Node) Main.getInstance().getAssetManager().loadModel("Models/Gore/goreLeg1.j3o");
        ParticleUtils.spawnGorePhysicalParticleShaded(goreLeg1, node.getWorldTranslation().add(new Vector3f(-0.6f, 0.6f, -0.1f)));
        guts.add(gore);
        guts.add(gore1);
        guts.add(gore2);
        guts.add(goreHand);
        guts.add(goreHand1);
        guts.add(goreHand2);
        guts.add(goreHead);
        guts.add(goreHead1);
        guts.add(goreLeg);
        guts.add(goreLeg1);
        Random r = new Random();
        for (Node gut : guts) {
            gut.scale(1 + r.nextFloat(0.25f));
        }
        node.removeFromParent();
        ClientGameAppState.getInstance().getGrid().remove(this);
        hideHitboxIndicator();
    }

    @Override
    public void attack() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void receiveDamageClient(DamageReceiveData damageData) {
        for(var onDamageReceivedEffect : onDamageReceivedEffects){
            onDamageReceivedEffect.applyClient(damageData);
        }

        setHealth(getHealth()-calculateDamage(damageData.getRawDamage()));
        var notMe = this != ClientGameAppState.getInstance().getPlayer();
        ParticleEmitter blood = EmitterPooler.getBlood();
        Vector3f bloodPos = node.getWorldTranslation().clone().add(0, 2, 0);
        blood.setLocalTranslation(bloodPos);
        if (getHealth() <= 0) {
            if (notMe) {
                blood.emitParticles(RANDOM.nextInt(30,50));
            }
            die();
            destroyClient();
            onDeathClient();
        } else if (notMe) {
            blood.emitParticles(RANDOM.nextInt(4,10));
        }

    }

    @Override
    public void receiveDamageServer(DamageReceiveData damageData) {
        for(var onDamageReceivedEffect : onDamageReceivedEffects){
            onDamageReceivedEffect.applyServer(damageData);
        }

        setHealth(getHealth()-calculateDamage(damageData.getRawDamage()));

        if (getHealth() <= 0) {
            destroyServer();
            onDeathServer();
            return;
        }
        Main.getInstance().enqueue(() -> {
            notifyEventSubscribers(new DamageReceivedEvent(damageData));
        }
        );
    }

    @Override
    public void notifyServerAboutReceivingDamage(DamageReceiveData damageReceiveData) {
        DestructibleDamageReceiveMessage hpUpd = new DestructibleDamageReceiveMessage(damageReceiveData);
        hpUpd.setReliable(true);
        ClientGameAppState.getInstance().getClient().send(hpUpd);
    }

    public Holdable getEquippedRightHand() {
        return equippedRightHand;
    }

    public void setEquippedRightHand(Holdable equippedRightHand) {
        this.equippedRightHand = equippedRightHand;
    }

    public Holdable getEquippedLeftHand() {
        return equippedLeftHand;
    }

    public void setEquippedLeftHand(Holdable equippedLeftHand) {
        this.equippedLeftHand = equippedLeftHand;
    }

    public SkinningControl getSkinningControl() {
        return skinningControl;
    }

    @Override
    public void equip(Item item) {
        if (item instanceof Equippable equippableItem) {
            equippableItem.humanMobEquipClient(this);
        }
    }

    @Override
    public void unequip(Item item) {
        if (item instanceof Equippable equippableItem) {
            equippableItem.humanMobUnequipClient(this);
        }
    }

    @Override
    public void equipServer(Item e) {
        if (e instanceof Equippable equippableItem) {
            equippableItem.serverEquip(this);
        }
    }

    @Override
    public void unequipServer(Item e) {
        if (e instanceof Equippable equippableItem) {
            equippableItem.serverUnequip(this);
        }
    }

    @Override
    public AbstractMessage createNewEntityMessage() {
        NewMobMessage msg = new NewMobMessage(this, node.getWorldTranslation(), mobSpawnType);
        msg.setReliable(true);
        return msg;
    }

    @Override
    public float getArmorValue() {
        return helmet.getArmorValue() + vest.getArmorValue()
                + gloves.getArmorValue()
                + boots.getArmorValue();
    }

    @Override
    public float calculateDamage(float damage) {
        float reducedDmg = damage - getArmorValue();
        return reducedDmg > 0 ? reducedDmg : 0;
//return 0;
    }

    @Override
    protected final void createHitbox() {
        float hitboxWidth = 0.5f;
        float hitboxHeight = 1.25f;
        float hitboxLength = 0.5f;
        hitboxNode.move(0, hitboxHeight, 0);
        collisionShape = new RectangleAABB(hitboxNode.getWorldTranslation(), hitboxWidth, hitboxHeight, hitboxLength);
        showHitboxIndicator();
    }

    @Override
    public void setPositionClient(Vector3f newPos) {
        setServerLocation(newPos);
        setPosInterpolationValue(1.f);
        WorldGrid grid = ClientGameAppState.getInstance().getGrid();
        grid.remove(this);
        node.setLocalTranslation(newPos);
        grid.insert(this);
        modelComposer.setCurrentAction("Idle", "Legs");
    }

    @Override
    public void setPositionServer(Vector3f newPos) {
        WorldGrid grid = ServerGameAppState.getInstance().getGrid();
        grid.remove(this);
        node.setLocalTranslation(newPos);
        grid.insert(this);
    }

    @Override
    public void moveServer(Vector3f moveVec) {
        super.moveServer(moveVec);
        positionChangedOnServer.set(true);
    }

    @Override
    public void onCollisionClient(Collidable other) {
    }

    @Override
    public void onCollisionServer(Collidable other) {
    }

    @Override
    public void interpolateRotation(float tpf) {
        setRotInterpolationValue(Math.min(rotInterpolationValue + MOB_ROTATION_RATE * tpf, 1));

        node.getLocalRotation().nlerp(ClientSynchronizationUtils.GetYAxisRotation(serverRotation), rotInterpolationValue);
        node.setLocalRotation(node.getLocalRotation());

        var handsJoint = skinningControl.getArmature().getJoint("HandsRotationBone");
        handsJoint.getLocalRotation().nlerp(
                ClientSynchronizationUtils.GetXAxisRotation(getServerRotation()), rotInterpolationValue
        );

        skinningControl.getArmature().getJoint("Head").getLocalTransform().setRotation(handsJoint.getLocalRotation());
    }

    @Override
    public void setPosInterpolationValue(float posInterpolationValue) {
        super.setPosInterpolationValue(posInterpolationValue);

//        System.out.println("posInterpolationValue " + posInterpolationValue);
//        System.out.println("modelComposer.getLayer(\"Legs\").getTime() " + modelComposer.getLayer("Legs").getTime());
        if (!modelComposer.getLayer("Legs").getCurrentActionName().equals("Run")) {
            modelComposer.setCurrentAction("Run", "Legs");
            modelComposer.getLayer("Legs").getCurrentAction().setSpeed(2);
        }

    }

    public void addAi() {

        var attack = new LeafNode(new Attack());

        List<BehaviorNode> children = Arrays.asList(
                new LeafNode(new GetCurrentTimestamp()),
                new LeafNode(new RotateToDesiredRotation()),
                new SelectorNode(Arrays.asList(
                        new SequenceNode(Arrays.asList(
                                new LeafNode(new CheckForTarget()),
                                new LeafNode(new ResetPath()),
                                new SelectorNode(Arrays.asList(
                                        attack,
                                        new SequenceNode(Arrays.asList(
                                                new LeafNode(new MoveInRange()),
                                                attack
                                        ))
                                ))
                        ))
                )),
                new SelectorNode(Arrays.asList(
                        new SequenceNode(Arrays.asList(
                                new LeafNode(new IsPathfindingNeeded()),
                                new LeafNode(new Pathfind())
                        )),
                        new SequenceNode(Arrays.asList(
                                new LeafNode(new ShouldLookIntoRandomDirection()),
                                new LeafNode(new SetRandomLookDirection())
                        )),
                        new SequenceNode(Arrays.asList(
                                new LeafNode(new WalkAction()),
                                new LeafNode(new ResetPath())
                        ))
                ))
        );

        var rootNode = new ParallelNode(children);
        var context = new SimpleHumanMobContext(this);
        addEventSubscriber(context);
        behaviorTree = new BehaviorTree(rootNode, context);

    }

    @Override
    public void destroyServer() {
        Main.getInstance().enqueue(() -> {
            removeEntityByIdServer(id);
            var server = ServerGameAppState.getInstance();
            server.getGrid().remove(this);
            if (node.getParent() != null) {
                node.removeFromParent();
            }
        });
    }

    @Override
    public void destroyClient() {
        Main.getInstance().enqueue(() -> {
            removeEntityByIdClient(id);
            var client = ClientGameAppState.getInstance();
            client.getGrid().remove(this);
            if(node.getParent() != null) {
                node.removeFromParent();
            }
        });
    }

    @Override
    public void playAnimation(Animation animation) {
        switch (animation) {
            case HUMAN_ATTACK_MELEE:
                var setNotLoopingAndSpeedUp = new InvokeMethodTween(() -> {
                    modelComposer.getCurrentAction("Hands").setSpeed(2f);
                    modelComposer.getLayer("Hands").setLooping(false);
                });

                modelComposer.actionSequence("fullSwing",
                        Tweens.parallel(modelComposer.action("WindupSwipe1H"), setNotLoopingAndSpeedUp),
                         Tweens.parallel(modelComposer.action("AttackSwipe1H"), setNotLoopingAndSpeedUp)
                );

                modelComposer.setCurrentAction("fullSwing", "Hands");
//                modelComposer.getCurrentAction().setSpeed(1.25f);
                break;

            default:
                break;
        }
    }

    @Override
    public void dealDamageClient(float damage, Destructible target) {
        var targetDamageReceiveData = new DamageReceiveData(target.getId(),id,damage);

        for(var onHitEffect : onDealDamageEffects){
            onHitEffect.applyClient(targetDamageReceiveData);
        }

        target.onAttacked(this, targetDamageReceiveData);
    }

    @Override
    public void dealDamageServer(DamageReceiveData damageReceiveData, Destructible target) {
        for(var onHitEffect : onDealDamageEffects){
            onHitEffect.applyServer(damageReceiveData);
        }

        target.receiveDamageServer(damageReceiveData);
    }

    @Override
    public void onAttacked(Mob shooter, DamageReceiveData damage) {
        notifyServerAboutReceivingDamage(damage);
    }

}
