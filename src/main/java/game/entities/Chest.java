package game.entities;

import client.appStates.ClientGameAppState;
import static client.appStates.ClientGameAppState.removeEntityByIdClient;
import client.Main;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.scene.Node;
import data.DamageReceiveData;
import game.entities.DecorationTemplates.DecorationTemplate;
import static game.entities.DestructibleUtils.attachDestructibleToNode;
import static game.entities.DestructibleUtils.setupModelShootability;
import game.entities.mobs.Mob;
import game.items.Item;
import static game.map.blocks.VoxelLighting.setupModelLight;
import game.map.collision.RectangleAABB;
import game.map.collision.WorldGrid;
import lombok.Getter;
import messages.DestructibleDamageReceiveMessage;
import messages.NewChestMessage;
import server.ServerGameAppState;
import static server.ServerGameAppState.removeEntityByIdServer;

public class Chest extends StatusEffectContainer {
    @Getter
    private final Item[] equipment = new Item[9];
    private boolean locked;

    private static final DecorationTemplate TEMPLATE = DecorationTemplates.SMALL_FLAT_CRATE;

    public Chest(int id, String name, Node node) {
        super(id, name, node);
        locked = true;

        createHitbox();
    }

    public Item addToEquipment(Item item) {
        for (int i = 0; i < equipment.length; i++) {
            if (equipment[i] == null) {
                equipment[i] = item;
                break;
            }
        }
        return item;
    }

    public Item removeFromEquipment(Item item) {
        for (int i = 0; i < equipment.length; i++) {
            if (equipment[i] != null && equipment[i] == item) {
                equipment[i] = null;
                break;
            }
        }
        return item;
    }

    public static Chest createRandomChestClient(int id, Node parentNode, Vector3f offset, AssetManager a) {
        Node node = (Node) a.loadModel(TEMPLATE.getModelPath());
        Chest chest = new Chest(id, "Crate " + id, node);
        chest.hitboxNode.scale(1.25f);
        attachDestructibleToNode(chest, parentNode, offset);
        setupModelShootability(node, id);
        setupModelLight(node);
        return chest;
    }

    public static Chest createRandomChestServer(int id, Node parentNode, Vector3f offset, AssetManager a) {
        Node node = (Node) a.loadModel(TEMPLATE.getModelPath());
        Chest chest = new Chest(id, "Crate " + id, node);
        attachDestructibleToNode(chest, parentNode, offset);
        return chest;
    }

    @Override
    public void onInteract() {
        System.out.println("This " + name + " might contain valuable loot.");
    }

    @Override
    public void onAttacked(Mob shooter, DamageReceiveData damageReceiveData) {
        notifyServerAboutReceivingDamage(damageReceiveData);
    }

    @Override
    public void receiveDamageClient(DamageReceiveData damageData) {
        for(var onDamageReceivedEffect : onDamageReceivedEffects){
            onDamageReceivedEffect.applyClient(damageData);
        }

        setHealth(getHealth()-damageData.getRawDamage());

        if (getHealth() <= 0) {
            die();
            destroyClient();
            onDeathClient();
        }
    }

    @Override
    public void receiveDamageServer(DamageReceiveData damageData) {
        for(var onDamageReceivedEffect : onDamageReceivedEffects){
            onDamageReceivedEffect.applyServer(damageData);
        }

        setHealth(getHealth()-damageData.getRawDamage());

        if (getHealth() <= 0) {
            destroyServer();
            onDeathServer();
        }
    }

    @Override
    public void die() {
        for (int i = 0; i < equipment.length; i++) {
            Item item = equipment[i];
            if (item != null) {
                item.drop(node.getWorldTranslation().add(0, 0.3f, 0));
            }
            equipment[i] = null;
        }
        node.removeFromParent();
        ClientGameAppState.getInstance().getGrid().remove(this);
        hideHitboxIndicator();
        destroyClient();
    }

    @Override
    public AbstractMessage createNewEntityMessage() {
        NewChestMessage msg = new NewChestMessage(this, node.getWorldTranslation());
        msg.setReliable(true);
        return msg;
    }

    @Override
    public float getArmorValue() {
        return 0;
    }

    @Override
    public float calculateDamage(float damage) {
        float reducedDmg = damage - getArmorValue();
        return reducedDmg > 0 ? reducedDmg : 0;
    }

    @Override
    protected final void createHitbox() {

        hitboxNode.move(0, TEMPLATE.getCollisionShapeHeight(), 0);
        collisionShape = new RectangleAABB(hitboxNode.getWorldTranslation(), TEMPLATE.getCollisionShapeWidth(), TEMPLATE.getCollisionShapeHeight(), TEMPLATE.getCollisionShapeLength());
        showHitboxIndicator();

    }

    @Override
    public void setPositionClient(Vector3f newPos) {
        ClientGameAppState.getInstance().getGrid().remove(this);
        node.setLocalTranslation(newPos);
        ClientGameAppState.getInstance().getGrid().insert(this);
    }

    @Override
    public void setPositionServer(Vector3f newPos) {
        WorldGrid grid = ServerGameAppState.getInstance().getGrid();
        grid.remove(this);
        node.setLocalTranslation(newPos);
        grid.insert(this);
    }

    @Override
    public void moveClient(float tpf) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void notifyServerAboutReceivingDamage(DamageReceiveData damageReceiveData) {
        DestructibleDamageReceiveMessage hpUpd = new DestructibleDamageReceiveMessage(damageReceiveData);
        hpUpd.setReliable(true);
        ClientGameAppState.getInstance().getClient().send(hpUpd);
    }

    enum ChestType {
        COMMON_LOOT_CHEST,
        WEAK_LOOT_CHEST,
        MEDIUM_LOOT_CHEST,
        GOOD_LOOT_CHEST
    }

    @Override
    public void onCollisionClient(Collidable other) {
    }

    @Override
    public void onCollisionServer(Collidable other) {
    }

    @Override
    public void destroyServer() {
        var server = ServerGameAppState.getInstance();
        server.getGrid().remove(this);
        if (node.getParent() != null) {
            Main.getInstance().enqueue(() -> {
                node.removeFromParent();
            });
        }
        removeEntityByIdServer(id);
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
