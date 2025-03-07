package game.entities.mobs.player;

import game.entities.factories.MobSpawnType;
import game.entities.inventory.Hotbar;
import game.items.Equippable;
import game.items.Item;

import client.appStates.ClientGameAppState;
import com.jme3.anim.SkinningControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import game.entities.Attribute;
import game.entities.FloatAttribute;
import FirstPersonHands.FirstPersonHands;
import guiComponents.LemurPlayerInventoryGui;
import guiComponents.LemurPlayerHealthbar;
import static client.appStates.ClientGameAppState.removeEntityByIdClient;
import client.Main;
import static client.Main.CAM_ROT_SPEED;
import static client.Main.CAM__MOVE_SPEED;
import com.jme3.anim.AnimComposer;
import data.DamageReceiveData;
import game.entities.mobs.HumanMob;
import game.entities.mobs.playerClasses.AssaultClass;
import game.entities.mobs.playerClasses.MedicClass;
import game.entities.mobs.playerClasses.PlayerClass;
import game.map.collision.WorldGrid;
import lombok.Getter;
import lombok.Setter;
import messages.PlayerPosUpdateRequest;
import server.ServerGameAppState;
import static server.ServerGameAppState.removeEntityByIdServer;

public class Player extends HumanMob {
    private static boolean isPlayerControlsEnabled = true;

    @Getter
    @Setter
    public float identifyRange = 8;

    @Getter
    @Setter
    private float pickupRange = 8;

    private static final int HOTBAR_SIZE = 10;

    @Getter
    @Setter
    private boolean forward, backward, right, left, holdsTrigger;

    // camera
    private CameraNode mainCameraNode;
    private CameraNode firstPersonCameraNode;
    private Node rotationNode = new Node();

    private Camera mainCamera;
    private final Hotbar hotbar;
    private final Node gunNode = new Node("gun node");

    //controlling player actions
    private boolean viewingEquipment;
    private boolean cameraMovementLocked;
    private boolean movementControlLocked;

    @Getter
    @Setter
    protected Item lastTargetedItem;

    @Getter
    @Setter
    protected ViewPort gunViewPort;

    @Getter
    @Setter
    protected FirstPersonHands firstPersonHands;

    @Getter
    private final PlayerClass playerClass;

    @Getter
    @Setter
    private LemurPlayerHealthbar playerHealthbar;

    @Getter
    private LemurPlayerInventoryGui playerinventoryGui;

    @Override
    public void equip(Item item) {
        if (item instanceof Equippable equippableItem) {
            equippableItem.playerEquipClient(this);
        }
    }

    @Override
    public void unequip(Item item) {
        if (item instanceof Equippable equippableItem) {
            equippableItem.playerUnequipClient(this);
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

    public Node getGunNode() {
        return gunNode;
    }

    public Player(int id, Node node, String name, Camera mainCamera, SkinningControl skinningControl, AnimComposer composer, PlayerClass playerClass) {
        super(MobSpawnType.SOLDIER, id, node, name, skinningControl, composer);
        this.playerClass = playerClass;
        this.mainCamera = mainCamera;
        firstPersonHands = new FirstPersonHands(this);
        hotbar = new Hotbar(new Item[HOTBAR_SIZE]);

        if (playerClass instanceof AssaultClass) {
            setHealth(25);
            setMaxHealth(25);
        } else if (playerClass instanceof MedicClass) {
            setHealth(20);
            setMaxHealth(20);
        } else {
            setHealth(35);
            setMaxHealth(35);
        }

        cachedSpeed = 11.25f;
        attributes.put(SPEED_ATTRIBUTE_KEY, new FloatAttribute(cachedSpeed));

    }

    @Override
    public void receiveDamageClient(DamageReceiveData damageData) {
        float previousHealth = getHealth();
        super.receiveDamageClient(damageData);
        if (playerHealthbar != null) {
            float normalizedPercentHealth = getHealth() / getMaxHealth();
            float normalizedChange = (previousHealth - getHealth()) / getMaxHealth();
            playerHealthbar.setHealthbarParams(normalizedPercentHealth, normalizedChange);
        }
    }

    @Override
    public void receiveHealClient(float heal) {
        float previousHealth = getHealth();
        super.receiveHealClient(heal);

        if (playerHealthbar != null) {
            float normalizedPercentHealth = getHealth() / getMaxHealth();
            float normalizedChange = (previousHealth - getHealth()) / getMaxHealth();
            playerHealthbar.setHealthbarParams(normalizedPercentHealth, normalizedChange);
        }
    }

    public Hotbar getHotbar() {
        return hotbar;
    }

    public Camera getMainCamera() {
        return mainCamera;
    }

    public void setMainCamera(Camera mainCamera) {
        this.mainCamera = mainCamera;
    }

    public CameraNode getMainCameraNode() {
        return mainCameraNode;
    }

    public void setMainCameraNode(CameraNode mainCameraNode) {
        this.mainCameraNode = mainCameraNode;
    }

    public CameraNode getFirstPersonCameraNode() {
        return firstPersonCameraNode;
    }

    public void setFirstPersonCameraNode(CameraNode firstPersonCameraNode) {
        this.firstPersonCameraNode = firstPersonCameraNode;
    }

    public Node getRotationNode() {
        return rotationNode;
    }

    public void setRotationNode(Node rotationNode) {
        this.rotationNode = rotationNode;
    }

    @Override
    public void moveClient(float tpf) {
        var cm = ClientGameAppState.getInstance();

        if ((forward || backward || left || right) && !isMovementControlLocked()) {
            movementVector.set(0, 0, 0);

            WorldGrid collisionGrid = cm.getGrid();

            collisionGrid.remove(this);

            if (forward) {

                Vector3f moveVec = getNode().getLocalRotation().getRotationColumn(2).mult(cachedSpeed * tpf).clone();
                movementVector.addLocal(moveVec);

            }
            if (backward) {

                Vector3f moveVec = getNode().getLocalRotation().getRotationColumn(2).mult(0.5f*cachedSpeed * tpf).clone().negateLocal();
                movementVector.addLocal(moveVec);

            }
            if (left) {

                Vector3f moveVec = cm.getCamera().getLeft().mult(cachedSpeed * tpf);
                movementVector.addLocal(moveVec);
            }
            if (right) {

                Vector3f moveVec = cm.getCamera().getLeft().negateLocal().mult(cachedSpeed * tpf);
                movementVector.addLocal(moveVec);
            }

            movementVector.setY(0);

            if (wouldNotCollideWithSolidEntitiesAfterMoveClient(new Vector3f(0, 0, movementVector.getZ()))) {
                node.move(0, 0, movementVector.getZ());
            }

            if (wouldNotCollideWithSolidEntitiesAfterMoveClient(new Vector3f(movementVector.getX(), 0, 0))) {
                node.move(movementVector.getX(), 0, 0);
            }
//            if (node.getWorldTranslation().distance(serverLocation) > cachedSpeed * tpf) {
            PlayerPosUpdateRequest posu = new PlayerPosUpdateRequest(cm.getCurrentGamemode().getLevelManager().getCurrentLevelIndex(),id, node.getWorldTranslation());
            cm.getClient().send(posu);
//            }

            checkCollisionWithPassableEntitiesClient();
            collisionGrid.insert(this);

        }
    }

    public void setPlayerinventoryGui(LemurPlayerInventoryGui gui){
        this.playerinventoryGui = gui;
        this.getEquipment().setInventoryGui(gui);
        this.getHotbar().setInventoryGui(gui);
    }

    @Override
    public void die() {
        if (gunViewPort != null) {
            gunViewPort.detachScene(gunNode);
        }
        Main.getInstance().getFlyCam().setMoveSpeed(CAM__MOVE_SPEED);
        Main.getInstance().getFlyCam().setRotationSpeed(CAM_ROT_SPEED);

        super.die();
        forward = false;
        backward = false;
        left = false;
        right = false;
    }

    @Override
    public String toString() {
        return "Player{" + id + "} (visible name =  \"" + name + "\"  )";
    }

    public boolean isViewingEquipment() {
        return viewingEquipment;
    }

    public void setViewingEquipment(boolean viewingEquipment) {
        this.viewingEquipment = viewingEquipment;
    }

    public boolean isMovementControlLocked() {
        return movementControlLocked;
    }

    public void setMovementControlLocked(boolean movementControlLocked) {
        this.movementControlLocked = movementControlLocked;
    }

    public boolean isCameraMovementLocked() {
        return cameraMovementLocked;
    }

    public void setCameraMovementLocked(boolean cameraMovementLocked) {
        this.cameraMovementLocked = cameraMovementLocked;
    }

    @Override
    public void attributeChangedNotification(int attributeId, Attribute oldAttributeCopy, Attribute copyOfNewAttribute) {
        super.attributeChangedNotification(attributeId, oldAttributeCopy, copyOfNewAttribute);
        if (attributeId == SPEED_ATTRIBUTE_KEY) {
            cachedSpeed = ((FloatAttribute) copyOfNewAttribute).getValue();
        } else if(attributeId == MAX_HEALTH_ATTRIBUTE_KEY){
            if (playerHealthbar != null) {
                float normalizedPercentHealth = getHealth() / getMaxHealth();
                float maxHealthChangePercent = ((FloatAttribute)oldAttributeCopy).getValue()/((FloatAttribute)copyOfNewAttribute).getValue();
                playerHealthbar.setHealthbarParams(normalizedPercentHealth, 0);
                playerHealthbar.getNormalizedHealthPercentAndChange().setY(playerHealthbar.getNormalizedHealthPercentAndChange().getY()*maxHealthChangePercent);
            }
        }
    }

    @Override
    public void destroyServer() {
        /* cant leak player into grid because it removes the player from mob list immediately, and re-insert on move checks if the mobs still is in hashmap
        and the mob hashmap is thread safe */
        removeEntityByIdServer(id);
        var server = ServerGameAppState.getInstance();
        server.getGrid().remove(this);
        if (node.getParent() != null) {
            Main.getInstance().enqueue(() -> {
                node.removeFromParent();
            });
        }
    }

    @Override
    public void destroyClient() { // cannot leak player into grid because both move and destroyClient are invoked on main thread :)
        var client = ClientGameAppState.getInstance();
        client.getGrid().remove(this);
        Main.getInstance().enqueue(() -> {
            node.removeFromParent();
        });
        removeEntityByIdClient(id);
    }

    public static synchronized void enablePlayerControls(){
        isPlayerControlsEnabled = true;
        if(ClientGameAppState.getInstance() != null) { // if player closes the game
            ClientGameAppState.getInstance().getInputController().initializeParamsAfterMouseEnable();
        }
    }

    public static synchronized void disablePlayerControls(){
        isPlayerControlsEnabled = false;
    }

    public static boolean isPlayerControlsEnabled(){
        return isPlayerControlsEnabled;
    }

}
