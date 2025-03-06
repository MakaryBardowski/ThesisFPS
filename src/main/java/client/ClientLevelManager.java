package client;

import static client.ClientSynchronizationUtils.interpolateGrenadePosition;
import static client.ClientSynchronizationUtils.interpolateMobPosition;

import client.appStates.ClientGameAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.light.AmbientLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.network.Client;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import data.jumpToLevelData.ClientJumpToLevelData;
import game.entities.Entity;
import game.entities.factories.AllMobFactory;
import game.entities.factories.MobSpawnType;
import game.entities.factories.PlayerFactory;
import game.entities.grenades.ThrownGrenade;
import game.entities.mobs.Mob;
import game.entities.mobs.player.Player;
import game.map.ClientLevelGenerator;
import game.map.MapType;
import game.map.blocks.Map;
import game.map.collision.WorldGrid;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import lombok.Setter;
import server.LevelManager;

public class ClientLevelManager extends LevelManager<ClientJumpToLevelData> {

    private static final Main MAIN_INSTANCE = Main.getInstance();
    private final ClientGameAppState GAME_APP_STATE = ClientGameAppState.getInstance();
    @Getter
    @Setter
    private Client client;

    @Getter
    private final AssetManager assetManager;

    @Getter
    private final RenderManager renderManager;

    @Getter
    private final InputManager inputManager;

    @Getter
    @Setter
    private Player player;

    @Getter
    private final ConcurrentHashMap<Integer, Entity> mobs = new ConcurrentHashMap<>();

    @Getter
    private WorldGrid grid;

    @Getter
    private game.map.Level level;

    @Getter
    private final int BLOCK_SIZE = 3;

    @Getter
    private final int COLLISION_GRID_CELL_SIZE = 18;

    @Getter
    private final int CHUNK_SIZE = 16;

    @Getter
    private final int MAP_SIZE_XZ = 20;

    @Getter
    private final int MAP_SIZE_Y = 20;

    @Getter
    private final Node rootNode = new Node("ROOT NODE");

    @Getter
    private final Node worldNode = new Node("WORLD NODE");

    @Getter
    private final Node mapNode = new Node("MAP NODE");

    @Getter
    private final Node debugNode = new Node("DEBUG NODE");

    @Getter
    private final Node destructibleNode = new Node("DESTRUCTIBLE NODE");

    @Getter
    private final Node entityNode = new Node("ENTITY NODE");

    @Getter
    private final Node pickableNode = new Node("PICKABLE NODE");

    @Getter
    @Setter
    private volatile Map nextStaticMap; // cached from server

    public ClientLevelManager() {
        this.assetManager = MAIN_INSTANCE.getAssetManager();
        this.renderManager = MAIN_INSTANCE.getRenderManager();
        this.inputManager = MAIN_INSTANCE.getInputManager();
    }

    public void initialize() {


        var trueRootNode = Main.getInstance().getRootNode();
        trueRootNode.attachChild(rootNode);

        worldNode.attachChild(debugNode);
        worldNode.attachChild(entityNode);
        pickableNode.attachChild(destructibleNode);
        entityNode.attachChild(pickableNode);
        worldNode.attachChild(mapNode);
        rootNode.attachChild(worldNode);

        float renderDistance = 700f;
        MAIN_INSTANCE.getCamera().setFrustumPerspective(70, (float) GAME_APP_STATE.getSettings().getWidth() / GAME_APP_STATE.getSettings().getHeight(), 0.01f, renderDistance);

        MAIN_INSTANCE.getViewPort().setBackgroundColor(ColorRGBA.Cyan.clone());
        MAIN_INSTANCE.getViewPort().setClearColor(true);

        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(0.5f));
        worldNode.addLight(al);

        grid = new WorldGrid(MAP_SIZE_XZ, BLOCK_SIZE, COLLISION_GRID_CELL_SIZE);
//        DebugUtils.drawGrid();

    }

    @Override
    public void jumpToLevel(ClientJumpToLevelData clientJumpToLevelData) {
        /* whole thing needs to be enqueued to main rendering thread because
        a) some messages rely on level index being correct. e.g the posUpdateRequest and player movement is also from main rendering thread
        b) model changes can only be ran from main rendering thread as per jme doc
         */
        Main.getInstance().enqueue( () -> {
            try {
                var levelIndex = clientJumpToLevelData.getLevelIndex();
                var newLevelSeed = clientJumpToLevelData.getLevelSeed();
                var newLevelType = clientJumpToLevelData.getLevelType();
                var playerSpawnpointsByPlayerId = clientJumpToLevelData.getPlayerSpawnpointsByPlayerId();

                currentLevelIndex = levelIndex;
                var clientLevelGenerator = new ClientLevelGenerator(newLevelSeed, newLevelType, BLOCK_SIZE, CHUNK_SIZE, mapNode);

                if (newLevelType.equals(MapType.FILE)) {
                    if (nextStaticMap != null) {
                        destroyCurrentLevel();
                        level = clientLevelGenerator.generateFromMap(nextStaticMap);
                        nextStaticMap = null;

                        for(var playerSpawnpointById : playerSpawnpointsByPlayerId.entrySet()){
                            var entity =  getMobs().get(playerSpawnpointById.getKey());
                            if(entity instanceof Player player){
                                player.setPositionClient(playerSpawnpointById.getValue());
                            }
                        }

                    } else {
                        Runnable awaitUntilCacheFilledAndCreateMap = () -> {
                            while (nextStaticMap == null) {
                                try {
                                    Thread.sleep(50);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            MAIN_INSTANCE.enqueue(() -> {
                                destroyCurrentLevel();
                                level = clientLevelGenerator.generateFromMap(nextStaticMap);
                                nextStaticMap = null;

                                for(var playerSpawnpointById : playerSpawnpointsByPlayerId.entrySet()){
                                    var entity =  getMobs().get(playerSpawnpointById.getKey());
                                    if(entity instanceof Player player){
                                        player.setPositionClient(playerSpawnpointById.getValue());
                                    }
                                }
                            });
                        };
                        Thread.ofVirtual().start(awaitUntilCacheFilledAndCreateMap);
                    }

                    return;
                }

                destroyCurrentLevel();
                level = clientLevelGenerator.generateLevel(MAP_SIZE_XZ, MAP_SIZE_Y, MAP_SIZE_XZ);

                for(var playerSpawnpointById : playerSpawnpointsByPlayerId.entrySet()){
                    var entity =  getMobs().get(playerSpawnpointById.getKey());
                    if(entity instanceof Player player){
                        player.setPositionClient(playerSpawnpointById.getValue());
                    }
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        });
    }

    public Mob registerMob(Integer id, MobSpawnType spawnType) {
        var mobFactory = new AllMobFactory(id,
                assetManager,
                destructibleNode);

        var p = mobFactory.createClientSide(spawnType);
        this.mobs.put(id, p);
        return p;
    }

    public void updateLoop(float tpf) {
        if (player != null) {
            if(player.getPlayerHealthbar() != null){
                player.getPlayerHealthbar().updateHealthbar(tpf);
            }
            player.moveClient(tpf);
            player.updateTemporaryEffectsClient();
            if (player.isHoldsTrigger() && player.getEquippedRightHand() != null) {
                player.getEquippedRightHand().playerUseInRightHand(player);
            }

        }

        mobs.values().forEach(x -> {
            if (x instanceof Mob m && x != player) {
                if (!m.getNode().getWorldTranslation().equals(m.getServerLocation())) {
                    grid.remove(m);
                    interpolateMobPosition(m, tpf);
                    grid.insert(m);
                }
                m.interpolateRotation(tpf);
            } else if (x instanceof ThrownGrenade g) {
                if (!g.getNode().getWorldTranslation().equals(g.getServerLocation())) {
                    interpolateGrenadePosition(g, tpf);
                }
            }
        }
        );
    }

    public Player registerPlayer(Integer id, boolean setAsPlayer, int playerClassIndex) {
        Player p = new PlayerFactory(id, destructibleNode, MAIN_INSTANCE.getCamera(), setAsPlayer).createClientSide(null, playerClassIndex);
        this.mobs.put(id, p);
        return p;
    }

    public <T extends Entity> T registerEntity(T entity) {
        this.mobs.put(entity.getId(), entity);
        return entity;
    }

    public FlyByCamera getFlyCam() {
        return MAIN_INSTANCE.getFlyByCamera();
    }

    public AppStateManager getStateManager() {
        return MAIN_INSTANCE.getStateManager();
    }

    private void destroyCurrentLevel(){
        if(level != null){
            level.getBlockWorld().getWorldNode().removeFromParent();
            level = null;
        }
    }

    @Override
    public void cleanup() {
        if(player != null && player.getGunViewPort() != null){
            player.getGunViewPort().detachScene(player.getGunNode());
            renderManager.removeMainView(player.getGunViewPort());
            player.getPlayerHealthbar().cleanup();
            player.getPlayerinventoryGui().cleanup();
        }
        rootNode.removeFromParent();
        Main.getInstance().getCamera().setLocation(new Vector3f(0,6,0.1f));
        Main.getInstance().getCamera().setRotation(new Quaternion(0,1,0,0));
    }
}
