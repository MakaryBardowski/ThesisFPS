package game.entities.factories;

import game.entities.mobs.player.Player;
import client.Main;
import com.jme3.anim.AnimComposer;
import com.jme3.anim.SkinningControl;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import game.entities.DestructibleUtils;
import game.entities.mobs.HumanMob;
import game.entities.mobs.playerClasses.PlayerClass;
import server.ServerGameAppState;
import statusEffects.EffectTemplates;

public class PlayerFactory extends MobFactory {

    private static final float PLAYER_HEIGHT = 2.12f * 0.975f;
    private final Camera mainCamera;
    private final Camera firstPersonCamera;
    private final RenderManager renderManager;
    private boolean setAsPlayer;
    private Vector3f playerSpawnpoint = new Vector3f(10, 3, 10);

    private static int nextSpawnedPlayerOffset = 0;

    //server constructor
    public PlayerFactory(int id, AssetManager assetManager, Node mobNode, RenderManager renderManager) {
        super(id, assetManager, mobNode);
        this.mainCamera = null;
        this.firstPersonCamera = null;
        this.renderManager = renderManager;

        playerSpawnpoint = new Vector3f(6 + nextSpawnedPlayerOffset, 3, 10 + nextSpawnedPlayerOffset);
        nextSpawnedPlayerOffset += 2;
    }

    //client constructor
    public PlayerFactory(int id, Node mobNode, Camera mainCamera, boolean setAsPlayer) {
        super(id, mobNode);
        this.mainCamera = mainCamera;
        this.firstPersonCamera = mainCamera.clone();
        this.renderManager = Main.getInstance().getRenderManager();
        this.setAsPlayer = setAsPlayer;
    }

    @Override
    public Player createClientSide(MobSpawnType spawnType, Object... creationData) { // mob spawn type doesnt matter for player
        PlayerClass pc = PlayerClass.getClassByIndex((int) creationData[0]);
        Player p = createPlayer(pc);
//        DestructibleUtils.attachDestructibleToNode(p, mobsNode, playerSpawnpoint);
        if (setAsPlayer) {
            setupFirstPersonCamera(p);
        }
        return p;
    }

    @Override
    public Player createServerSide(MobSpawnType spawnType, Object... creationData) { // mob spawn type doesnt matter for player
        var serverLevelManager = ServerGameAppState.getInstance().getCurrentGamemode().getLevelManager();
        PlayerClass pc = PlayerClass.getClassByIndex((int) creationData[0]);
        Player p = createPlayer(pc);
        DestructibleUtils.attachDestructibleToNode(p, mobsNode, playerSpawnpoint);

        var regenEffect = serverLevelManager.createAndRegisterStatusEffect(EffectTemplates.DEFAULT_REGENERATION,p);
        p.addEffect(regenEffect);

        return p;
    }

    private Player createPlayer(PlayerClass pc) {
        Node playerNode = loadPlayerModel();
        String name = "Player_" + id;
        SkinningControl skinningControl = getSkinningControl(playerNode);
        AnimComposer composer = getAnimComposer(playerNode);
        System.out.println("[PlayerFactory] create player id " + id);
        
        return new Player(id, playerNode, name, mainCamera, skinningControl, composer, pc);
    }

    private void setupFirstPersonCamera(Player p) {
        setupMainCamera(p);
        setupHandsCamera(p);
    }

    private Node loadPlayerModel() {
        Node node = (Node) assetManager.loadModel(HumanMob.HUMAN_SKELETON_RIG_PATH);
        return node;
    }

    private SkinningControl getSkinningControl(Node node) {
        return node.getChild(0).getControl(SkinningControl.class);
    }

    private AnimComposer getAnimComposer(Node node) {
        return node.getChild(0).getControl(AnimComposer.class);
    }

    private void setupMainCamera(Player p) {
        CameraNode playerCameraNode = new CameraNode("Main Camera Node" + p.getId(), mainCamera);
        p.getRotationNode().setName("player " + p.getId() + " rotation node");
        p.getRotationNode().attachChild(playerCameraNode);
        p.getNode().attachChild(p.getRotationNode());
        p.getRotationNode().setLocalTranslation(0, PLAYER_HEIGHT, 0);
        p.setMainCameraNode(playerCameraNode);
    }

    private void setupHandsCamera(Player p) {
        CameraNode gunCameraNode = new CameraNode("Gun Camera Node" + p.getId(), firstPersonCamera);
        ViewPort view2 = renderManager.createMainView("View of firstPersonCamera", firstPersonCamera);
        view2.setClearFlags(false, true, true);
        view2.attachScene(p.getGunNode());
        p.setGunViewPort(view2);
        System.out.println("setting frustum");
        firstPersonCamera.setFrustumPerspective(45f, (float) firstPersonCamera.getWidth() / firstPersonCamera.getHeight(), 0.01f, 300f);
        p.getRotationNode().attachChild(gunCameraNode);
        gunCameraNode.attachChild(p.getGunNode());

        gunCameraNode.setCullHint(Spatial.CullHint.Never);
        p.setFirstPersonCameraNode(gunCameraNode);
    }

}
