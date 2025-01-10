package game.entities.mobs;

import behaviorTree.BehaviorNode;
import behaviorTree.BehaviorTree;
import behaviorTree.LeafNode;
import behaviorTree.actions.MudBeetleActions.AttackAction;
import behaviorTree.actions.MudBeetleActions.CheckForTargetAction;
import behaviorTree.actions.MudBeetleActions.IsPathfindingNeededAction;
import behaviorTree.actions.MudBeetleActions.MoveInRangeAction;
import behaviorTree.actions.MudBeetleActions.PathfindAction;
import behaviorTree.actions.MudBeetleActions.ResetPathAction;
import behaviorTree.actions.MudBeetleActions.WalkAction;
import behaviorTree.composite.ParallelNode;
import behaviorTree.composite.SelectorNode;
import behaviorTree.composite.SequenceNode;
import behaviorTree.context.MudBeetleContext;
import game.effects.EmitterPooler;
import game.entities.Destructible;
import game.items.Item;
import game.map.collision.WorldGrid;
import client.ClientGameAppState;
import static client.ClientGameAppState.removeEntityByIdClient;
import client.ClientSynchronizationUtils;
import client.Main;
import com.jme3.anim.AnimComposer;
import com.jme3.anim.ArmatureMask;
import com.jme3.anim.SkinningControl;
import com.jme3.effect.ParticleEmitter;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.scene.Node;
import data.DamageReceiveData;
import static game.effects.DecalProjector.projectFromTo;
import game.effects.ParticleUtils;
import game.entities.Collidable;
import game.entities.FloatAttribute;
import game.entities.factories.MobSpawnType;
import game.map.collision.RectangleAABB;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import lombok.Getter;
import messages.DestructibleDamageReceiveMessage;
import messages.NewMobMessage;
import server.ServerMain;
import static server.ServerMain.removeEntityByIdServer;

public class MudBeetle extends Mob {

    protected SkinningControl skinningControl;

    @Getter
    private AnimComposer modelComposer;

    public MudBeetle(int id, Node node, String name, SkinningControl skinningControl, AnimComposer modelComposer) {
        super(id, node, name);

        maxHealth = 8;
        health = 8;
        
        cachedSpeed = 6;
        attributes.put(SPEED_ATTRIBUTE, new FloatAttribute(cachedSpeed));

        this.skinningControl = skinningControl;
        this.modelComposer = modelComposer;

        var armature = skinningControl.getArmature();
        ArmatureMask mask = new ArmatureMask();
        mask.addBones(armature, "Leg1L");
        mask.addBones(armature, "Leg2L");
        mask.addBones(armature, "Leg3L");
        mask.addBones(armature, "Leg1R");
        mask.addBones(armature, "Leg2R");
        mask.addBones(armature, "Leg3R");

        mask.addBones(armature, "Root");
        modelComposer.makeLayer("Legs", mask);
        modelComposer.setCurrentAction("Idle", "Legs");
        modelComposer.setCurrentAction("Idle", "Default");

        createHitbox();
//                var radiusVisualizer = createCircle(16, ColorRGBA.randomColor());
//        radiusVisualizer.move(0,0.2f,0);
//        node.attachChild(radiusVisualizer);
    }

    public void addAi() {
        List<BehaviorNode> children = Arrays.asList(
                new SequenceNode(Arrays.asList(
                        new LeafNode(new CheckForTargetAction()),
                        new LeafNode(new ResetPathAction()),
                        new SelectorNode(Arrays.asList(
                                new LeafNode(new AttackAction()),
                                new SequenceNode(Arrays.asList(
                                        new LeafNode(new MoveInRangeAction()),
                                        new LeafNode(new AttackAction())
                                ))
                        ))
                )),
                new SelectorNode(Arrays.asList(
                        new SequenceNode(Arrays.asList(
                                new LeafNode(new IsPathfindingNeededAction()),
                                new LeafNode(new PathfindAction())
                        )),
                        new SequenceNode(Arrays.asList(
                                new LeafNode(new WalkAction()),
                                new LeafNode(new ResetPathAction())
                        ))
                ))
        );

        var rootNode = new ParallelNode(children);
        var context = new MudBeetleContext(this);
        behaviorTree = new BehaviorTree(rootNode, context);

    }

    @Override
    public void interpolateRotation(float tpf) {
        setRotInterpolationValue(Math.min(rotInterpolationValue + MOB_ROTATION_RATE * tpf, 1));

        node.getLocalRotation().nlerp(ClientSynchronizationUtils.GetYAxisRotation(serverRotation), rotInterpolationValue);
        node.setLocalRotation(node.getLocalRotation());
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
    public void onAttacked(Mob shooter, DamageReceiveData damageReceiveData) {
        notifyServerAboutReceivingDamage(damageReceiveData);
    }

    @Override
    protected void createHitbox() {
        float hitboxWidth = 0.4f;
        float hitboxHeight = 0.4f;
        float hitboxLength = 0.4f;
        hitboxNode.move(0, hitboxHeight, 0);
        collisionShape = new RectangleAABB(hitboxNode.getWorldTranslation(), hitboxWidth, hitboxHeight, hitboxLength);
        showHitboxIndicator();
    }

    @Override
    public void onCollisionClient(Collidable other) {
    }

    @Override
    public void onCollisionServer(Collidable other) {
    }

    @Override
    public void onInteract() {
        System.out.println("The " + name + " burps!");
    }

    @Override
    public void setPosition(Vector3f newPos) {
        setServerLocation(newPos);
        setPosInterpolationValue(1.f);
        WorldGrid grid = ClientGameAppState.getInstance().getGrid();
        grid.remove(this);
        node.setLocalTranslation(newPos);
        grid.insert(this);
    }

    @Override
    public void setPositionServer(Vector3f newPos) {
        WorldGrid grid = ServerMain.getInstance().getGrid();
        grid.remove(this);
        node.setLocalTranslation(newPos);
        grid.insert(this);
        positionChangedOnServer.set(true);
    }

    @Override
    public AbstractMessage createNewEntityMessage() {
        NewMobMessage msg = new NewMobMessage(this, node.getWorldTranslation(), MobSpawnType.MUD_BEETLE);
        msg.setReliable(true);
        return msg;
    }

    @Override
    public void receiveDamageClient(DamageReceiveData damageData) {
        for(var onDamageReceivedEffect : onDamageReceivedEffects){
            onDamageReceivedEffect.applyClient(damageData);
        }

        health -= calculateDamage(damageData.getRawDamage());

        ParticleEmitter blood = EmitterPooler.getBlood();
        Vector3f bloodPos = node.getWorldTranslation().clone().add(0, 1, 0);
        blood.setLocalTranslation(bloodPos);

        if (health <= 0) {
            blood.emitParticles(2);
            Main.getInstance().enqueue(() -> {
                projectFromTo(ClientGameAppState.getInstance(), node.getWorldTranslation().clone().add(0, 1, 0), new Vector3f(0, -1, 0), "Textures/Gameplay/Decals/testBlood" + new Random().nextInt(2) + ".png", new Random().nextInt(2) + 2f);

            });
            die();
            destroyClient();
            onDeathClient();
        } else {
            blood.emitParticles(2);
        }
    }
    
    @Override
    public void receiveDamageServer(DamageReceiveData damageData) {
        for(var onDamageReceivedEffect : onDamageReceivedEffects){
            onDamageReceivedEffect.applyServer(damageData);
        }

        health -= calculateDamage(damageData.getRawDamage());

        if (health <= 0) {
            destroyServer();
            onDeathServer();
        }
    }

    @Override
    public void die() {
        dropEquipment();
        Node gore = (Node) Main.getInstance().getAssetManager().loadModel("Models/Gore/goreTorso0.j3o");
        ParticleUtils.spawnGorePhysicalParticleShaded(gore, node.getWorldTranslation().add(new Vector3f(0, 0.1f, 0)));
        gore.scale(new Random().nextFloat(0.8f, 1.25f));
        node.removeFromParent();
        ClientGameAppState.getInstance().getGrid().remove(this);
        hideHitboxIndicator();
    }

    @Override
    public float getArmorValue() {
        return 0;
    }

    @Override
    public float calculateDamage(float damage) {
        return damage;
    }

    @Override
    public void onCollision() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

//    @Override
//    public boolean wouldNotCollideWithSolidEntitiesAfterMoveClient(Vector3f moveVec) {
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
//    }
    @Override
    public void attack() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void notifyServerAboutReceivingDamage(DamageReceiveData damageReceiveData) {
        DestructibleDamageReceiveMessage hpUpd = new DestructibleDamageReceiveMessage(damageReceiveData);
        hpUpd.setReliable(true);
        ClientGameAppState.getInstance().getClient().send(hpUpd);
    }

    @Override
    public void equip(Item e) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void unequip(Item e) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void equipServer(Item e) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void unequipServer(Item e) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void move(float tpf) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void moveServer(Vector3f moveVec) {
        super.moveServer(moveVec);
        positionChangedOnServer.set(true);
    }

    @Override
    public void destroyServer() {
        removeEntityByIdServer(id);
        var server = ServerMain.getInstance();
        server.getGrid().remove(this);
        if (node.getParent() != null) {
            Main.getInstance().enqueue(() -> {
                node.removeFromParent();
            });
        }
    }

    @Override
    public void destroyClient() {
        var client = ClientGameAppState.getInstance();
        client.getGrid().remove(this);
        Main.getInstance().enqueue(() -> {
            node.removeFromParent();
        });
        removeEntityByIdClient(id);
    }
}
