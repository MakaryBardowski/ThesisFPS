package game.cameraAndInput;

import com.jme3.math.*;
import game.entities.AttachedEntity;
import game.entities.mobs.player.Player;
import client.appStates.ClientGameAppState;
import client.Main;
import static client.Main.CAM_ROT_SPEED;
import static client.Main.CAM__MOVE_SPEED;
import client.PlayerHUDController;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.scene.Spatial;
import game.items.Item;
import game.items.weapons.Grenade;
import game.items.weapons.MeleeWeapon;
import game.items.weapons.RangedWeapon;
import menu.states.InventoryMenuState;
import menu.states.PauseMenuState;
import messages.MobRotUpdateMessage;
import server.ServerGameAppState;

public class InputController {
    private float MAX_VERTICAL_ROTATION_DEG = 80;
    private float MIN_VERTICAL_ROTATION_DEG = -80;

    private InputManager m;
    private HeadBobControl headBob;
    private static final float ONE_DEGREE = 0.0174f;
    private static final float NOTIFY_SERVER_THRESHOLD = ONE_DEGREE * 2;
    /*
    JME cursor position cannot be altered externally
     */
    private Vector2f prevJMEcursorPos;
    private Vector2f cursorPositionForPlayerRotation;
    private float deltaX;
    private float deltaY;
    private float centeredX;
    private float centeredY;
    private Quaternion newRotationQuat;
    private float currentMaxDeviationX = 0;
    private float currentMaxDeviationY = 0;
    private float proposedRotationY = 0;

    private boolean updatePlayersThirdPersonHandsRot = false;

    public void createInputListeners(ClientGameAppState gs) {
        m = gs.getInputManager();
        initKeys(m, initActionListener(gs), initAnalogListener(gs));
        var rotationNode = gs.getPlayer().getRotationNode();
        headBob = new HeadBobControl(gs.getPlayer(), rotationNode.getLocalTranslation());
        rotationNode.addControl(headBob);
    }

    private ActionListener initActionListener(final ClientGameAppState gs) {
        final Player player = gs.getPlayer();
        ActionListener actionListener = new ActionListener() {
            @Override
            public void onAction(String name, boolean keyPressed, float tpf) {
                if(name.equals("Esc") && !keyPressed){
                    var menus = gs.getMenuStateMachine();
                    if(menus.getCurrentState() == null) {
                        menus.requestState(new PauseMenuState());
                    } else {
                        System.out.println("request old state");
                        menus.requestPreviousState();
                    }
                }

                if(!Player.isPlayerControlsEnabled()){
                    return;
                }

                if (!player.isDead() && name.equals("W") && !keyPressed) {
                    player.setForward(false);
                    setMovingAnimationidle(player);

                } else if (!player.isDead() && name.equals("W")) {
                    player.setForward(true);
                    setMovingAnimationPlayer(player, 2.5f);

                }

                if (!player.isDead() && name.equals("S") && !keyPressed) {
                    player.setBackward(false);
                    setMovingAnimationidle(player);

                } else if (!player.isDead() && name.equals("S")) {
                    player.setBackward(true);
                    setMovingAnimationPlayer(player, 1.25f);
                }

                if (!player.isDead() && name.equals("A") && !keyPressed) {
                    player.setLeft(false);
                    setMovingAnimationidle(player);

                } else if (!player.isDead() && name.equals("A")) {
                    player.setLeft(true);
                    setMovingAnimationPlayer(player, 1.25f);

                }

                if (!player.isDead() && name.equals("D") && !keyPressed) {
                    player.setRight(false);
                    setMovingAnimationidle(player);
                } else if (!player.isDead() && name.equals("D")) {
                    player.setRight(true);
                    setMovingAnimationPlayer(player, 1.25f);

                }

                if (!player.isDead() && isHotbarName(name) && !keyPressed) {
                    int hotbarIndex = Integer.parseInt(name);
                    Item equippedItem = player.getHotbar().getItemAt(hotbarIndex);
                    player.equip(equippedItem);
                    PlayerHUDController.sendEquipMessageToServer(equippedItem);
                }

                // attack test
                if (!player.isViewingEquipment() && player.getEquippedRightHand() != null && name.equals("Attack") && !keyPressed) {
                    if (player.getEquippedRightHand() instanceof Grenade) { // if its a grenade, dont throw when released
                        player.getEquippedRightHand().playerUseInRightHand(player);
                    } else {
                        player.setHoldsTrigger(false);
                    }
                } else if (!player.isDead() && !player.isViewingEquipment() && player.getEquippedRightHand() != null && name.equals("Attack") && keyPressed) {
                    if (player.getEquippedRightHand() instanceof RangedWeapon || player.getEquippedRightHand() instanceof MeleeWeapon) { // if its a ranged weapon, set holds trigger which makes the auto shoot
                        player.setHoldsTrigger(true);
                    }
                }

                if (name.equals("I") && !gs.getPlayer().isDead() && !keyPressed) {
                    var menuStateMachine = gs.getMenuStateMachine();
                    if(menuStateMachine.isStateNull()) {
                        menuStateMachine.requestState(new InventoryMenuState(gs.getPlayer()));
                    } else {
                        menuStateMachine.requestState(null);
                    }
                }

                if (name.equals(
                        "E")
                        && !gs.getPlayer().isDead()
                        && !keyPressed) {
                    interact();
                }
                if (name.equals("R") && !gs.getPlayer().isDead() && !keyPressed) {
                    var equipped = gs.getPlayer().getEquippedRightHand();
                    if (equipped != null && equipped instanceof RangedWeapon ranged) {
                        ranged.reload(player);
                    }
                }

//                if (name.equals("2") && !keyPressed) {
//                    DEBUG_GEO.rotate(10 * FastMath.DEG_TO_RAD, 0, 0);
//                }
//
//                if (name.equals("3") && !keyPressed) {
//                    DEBUG_GEO.rotate(0, 0, 10 * FastMath.DEG_TO_RAD);
//                }
//                if (name.equals("4") && !keyPressed) {
//                    DEBUG_GEO.rotate(0, 10 * FastMath.DEG_TO_RAD, 0);
//                }
                if (name.equals("K") && !keyPressed) {
//                    GlobalSettings.isAiDebug = !GlobalSettings.isAiDebug;
                    if(ServerGameAppState.getInstance() != null) {
                        ServerGameAppState.getInstance().getLevelManagerMobs().forEach((key, value) -> System.err.println(value));
                    }

                    System.gc();
                    player.setRight(false);
                    player.setLeft(false);
                    player.setBackward(false);
                    player.setForward(false);

                    System.out.println("cheat!");
                    Main.getInstance().getFlyCam().setMoveSpeed(CAM__MOVE_SPEED);
                    Main.getInstance().getFlyCam().setRotationSpeed(CAM_ROT_SPEED);

                    player.getMainCameraNode().removeFromParent();
                    player.setPickupRange(10000f);

                    player.getFirstPersonHands().getHandsNode().setCullHint(Spatial.CullHint.Never);
                    player.getNode().setCullHint(Spatial.CullHint.Inherit);

                    player.setMovementControlLocked(true);
                    player.setCameraMovementLocked(true);
                    updatePlayersThirdPersonHandsRot = true;
//                    ServerMain.getInstance().getGrid().getContents().forEach((i,set)->{
//                    if(!set.isEmpty()){
//                        System.err.println("------------set "+i);
//                    System.err.println(set);
//                    }
//                    });
                }
            }
        };

        gs.setActionListener(actionListener);

        return actionListener;
    }

    private AnalogListener initAnalogListener(final ClientGameAppState gs) {
        final Player player = gs.getPlayer();

        initializeParamsAfterMouseEnable();

        AnalogListener analogListener = new AnalogListener() {
            Vector2f currentCursorPos = new Vector2f();
            @Override
            public void onAnalog(String name, float value, float tpf) {
                if(!Player.isPlayerControlsEnabled()){
                    return;
                }

                if (name.equals("MouseMovedX")) {
                    deltaX = gs.getInputManager().getCursorPosition().x - prevJMEcursorPos.x;
                    if (!gs.getPlayer().isViewingEquipment()) {
                        cursorPositionForPlayerRotation.setX(cursorPositionForPlayerRotation.x + deltaX);
                        centeredX = cursorPositionForPlayerRotation.x;
                        newRotationQuat = new Quaternion();

                        centeredX = -0.005f * centeredX;
                        newRotationQuat.fromAngles(0, centeredX, 0);
                        currentMaxDeviationX += centeredX;

                        player.getNode().setLocalRotation(newRotationQuat);
                    }
                    prevJMEcursorPos.setX(gs.getInputManager().getCursorPosition().x);

                }
                if (name.equals("MouseMovedY")) {
                    float cursorY = gs.getInputManager().getCursorPosition().y;
                    deltaY = cursorY - prevJMEcursorPos.y;

                    if (!gs.getPlayer().isViewingEquipment()) {
                        centeredY += deltaY;
                        float proposedRotationDeg = centeredY * -0.005f * FastMath.RAD_TO_DEG;
                        if (proposedRotationDeg > MAX_VERTICAL_ROTATION_DEG) {
                            proposedRotationDeg = MAX_VERTICAL_ROTATION_DEG;
                        } else if (proposedRotationDeg < MIN_VERTICAL_ROTATION_DEG) {
                            proposedRotationDeg = MIN_VERTICAL_ROTATION_DEG;
                        }

                        newRotationQuat = new Quaternion();
                        newRotationQuat.fromAngles(proposedRotationDeg * FastMath.DEG_TO_RAD, 0, 0);
                        player.getRotationNode().setLocalRotation(newRotationQuat);

                        centeredY = proposedRotationDeg / -0.005f / FastMath.RAD_TO_DEG;
                    }

                    prevJMEcursorPos.setY(cursorY);
                }
                if (currentMaxDeviationX >= NOTIFY_SERVER_THRESHOLD || currentMaxDeviationX <= -NOTIFY_SERVER_THRESHOLD || currentMaxDeviationY >= NOTIFY_SERVER_THRESHOLD || currentMaxDeviationY <= -NOTIFY_SERVER_THRESHOLD) {
                    currentMaxDeviationX = 0;
                    currentMaxDeviationY = 0;
                    MobRotUpdateMessage rotu = new MobRotUpdateMessage(player.getId(), player.getNode().getLocalRotation().mult(player.getRotationNode().getLocalRotation()));
                    ClientGameAppState.getInstance().getClient().send(rotu);

                    if (updatePlayersThirdPersonHandsRot) {
                        var handsRot = player.getRotationNode().getLocalRotation();

                        var skinningControl = player.getSkinningControl();

//                        player.getThirdPersonHandsNode().setLocalRotation(handsRot);
//                        var curr = ((Node) player.getThirdPersonHandsNode().getChild(0)).getChild(0).getLocalRotation();
//                        curr.set(handsRot.getX(), handsRot.getY(), handsRot.getZ(), handsRot.getW());
                        skinningControl.getArmature().getJoint("HandsRotationBone").getLocalTransform().setRotation(handsRot);

                        skinningControl.getArmature().getJoint("Head").getLocalTransform().setRotation(handsRot);
                    }
                }

            }

        };

        gs.setAnalogListener(analogListener);

        return analogListener;
    }

    public void initializeParamsAfterMouseEnable() {
        prevJMEcursorPos = ClientGameAppState.getInstance().getInputManager().getCursorPosition().clone().mult(1);
        if(cursorPositionForPlayerRotation == null) {
            cursorPositionForPlayerRotation = new Vector2f();
        }
    }

    private boolean isHotbarName(String s) {
        return s == "1" || s == "2" || s == "3" || s == "4" || s == "5" || s == "6" || s == "7" || s == "8" || s == "9" || s == "0";
    }

    private void initKeys(InputManager inputManager, ActionListener actionListener, AnalogListener analogListener) {

        inputManager.addMapping("W", new KeyTrigger(KeyInput.KEY_W)); // forward
        inputManager.addMapping("S", new KeyTrigger(KeyInput.KEY_S)); // backward
        inputManager.addMapping("A", new KeyTrigger(KeyInput.KEY_A)); // strafe left
        inputManager.addMapping("D", new KeyTrigger(KeyInput.KEY_D)); // strafe right
        inputManager.addMapping("Esc", new KeyTrigger(KeyInput.KEY_ESCAPE));

        inputManager.addMapping("Attack", new MouseButtonTrigger(MouseInput.BUTTON_LEFT)); // shoot
        inputManager.addMapping("AttackR", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT)); // shoot

        inputManager.addMapping("Grenade", new KeyTrigger(KeyInput.KEY_G));
        inputManager.addMapping("B", new KeyTrigger(KeyInput.KEY_B));
        inputManager.addMapping("E", new KeyTrigger(KeyInput.KEY_E)); // activate item
        inputManager.addMapping("I", new KeyTrigger(KeyInput.KEY_I)); // open EQ
        inputManager.addMapping("R", new KeyTrigger(KeyInput.KEY_R)); // strafe right

        inputManager.addMapping("K", new KeyTrigger(KeyInput.KEY_K));
        inputManager.addMapping("1", new KeyTrigger(KeyInput.KEY_1)); // hotbar 1
        inputManager.addMapping("2", new KeyTrigger(KeyInput.KEY_2)); // hotbar 2
        inputManager.addMapping("3", new KeyTrigger(KeyInput.KEY_3)); // hotbar 3
        inputManager.addMapping("4", new KeyTrigger(KeyInput.KEY_4)); // hotbar 4
        inputManager.addMapping("5", new KeyTrigger(KeyInput.KEY_5)); // hotbar 5
        inputManager.addMapping("6", new KeyTrigger(KeyInput.KEY_6)); // hotbar 6
        inputManager.addMapping("7", new KeyTrigger(KeyInput.KEY_7)); // hotbar 7
        inputManager.addMapping("8", new KeyTrigger(KeyInput.KEY_8)); // hotbar 8
        inputManager.addMapping("9", new KeyTrigger(KeyInput.KEY_9)); // hotbar 9
        inputManager.addMapping("0", new KeyTrigger(KeyInput.KEY_0)); // hotbar 0
        
        inputManager.addMapping("MouseMovedX",
                new MouseAxisTrigger(MouseInput.AXIS_X, false),
                new MouseAxisTrigger(MouseInput.AXIS_X, true)
        );
        inputManager.addMapping("MouseMovedY",
                new MouseAxisTrigger(MouseInput.AXIS_Y, false),
                new MouseAxisTrigger(MouseInput.AXIS_Y, true)
        );

        inputManager.addListener(actionListener,"Esc");
        inputManager.addListener(analogListener, "MouseMovedX");
        inputManager.addListener(analogListener, "MouseMovedY");
        inputManager.addListener(actionListener, "W");
        inputManager.addListener(actionListener, "S");
        inputManager.addListener(actionListener, "A");
        inputManager.addListener(actionListener, "D");
        inputManager.addListener(actionListener, "R");

        inputManager.addListener(actionListener, "Attack");
        inputManager.addListener(actionListener, "AttackR");

        inputManager.addListener(actionListener, "Grenade");
        inputManager.addListener(actionListener, "K");
        inputManager.addListener(actionListener, "B");
        inputManager.addListener(actionListener, "E");
        inputManager.addListener(actionListener, "I");

        inputManager.addListener(actionListener, "1");
        inputManager.addListener(actionListener, "2");
        inputManager.addListener(actionListener, "3");
        inputManager.addListener(actionListener, "4");
        inputManager.addListener(actionListener, "5");
        inputManager.addListener(actionListener, "6");
        inputManager.addListener(actionListener, "7");
        inputManager.addListener(actionListener, "8");
        inputManager.addListener(actionListener, "9");
        inputManager.addListener(actionListener, "0");

    }

    public static void destroyKeys(InputManager inputManager) {
        inputManager.clearMappings();
    }


    private void setMovingAnimationPlayer(Player p, float animSpeed) {
//        if (p.getHandsAnimChannel() != null && !p.getHandsAnimChannel().getAnimationName().equals("Run") || p.getHandsAnimChannel().getSpeed() < animSpeed) {
//            p.getHandsAnimChannel().setAnim("Run");
//            p.getHandsAnimChannel().setSpeed(animSpeed);
//        }
    }

    private void setMovingAnimationidle(Player p) {
//        if (p.getHandsAnimChannel() != null && !p.isForward() && !p.isBackward() && !p.isLeft() && !p.isRight()) {
//            p.getHandsAnimChannel().setAnim("Idle");
//            p.getHandsAnimChannel().setLoopMode(LoopMode.DontLoop);
//
//        }
    }

    private void interact() {
        ClientGameAppState cs = ClientGameAppState.getInstance();
        Player p = cs.getPlayer();
        CollisionResults results = new CollisionResults();
        Vector3f shotDirection = p.getMainCamera().getDirection();
        Vector3f shotOrigin = p.getMainCamera().getLocation();
        Ray ray = new Ray(shotOrigin, shotDirection);
        cs.getPickableNode().collideWith(ray, results);

        if (results.size() > 0) {
            CollisionResult closest = results.getClosestCollision();
            String hitName = closest.getGeometry().getName();
            if (hitName.matches("-?\\d+")) {
                Integer hitId = Integer.valueOf(hitName);
                var mobHit = (AttachedEntity) ClientGameAppState.getInstance().getMobs().get(hitId);
                if (closest.getContactPoint().distance(p.getNode().getWorldTranslation()) <= cs.getPlayer().getPickupRange()) {
                    mobHit.onInteract();
                }
            }
        }
    }

}
