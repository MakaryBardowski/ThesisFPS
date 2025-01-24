package client.appStates;

import client.ClientGameManager;
import client.ClientStoryGameManager;
import client.Main;
import client.PlayerHUD;
import com.jme3.network.service.serializer.ClientSerializerRegistrationsService;
import game.cameraAndInput.InputController;
import menu.MenuStateMachine;
import messages.messageListeners.ClientMessageListener;
import com.jme3.app.SimpleApplication;
import com.jme3.network.Client;
import com.jme3.network.Network;
import com.jme3.system.AppSettings;
import networkingUtils.NetworkingInitialization;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.network.ClientStateListener;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.ui.Picture;
import de.lessvoid.nifty.Nifty;
import game.entities.Entity;
import game.entities.factories.MobSpawnType;
import game.entities.mobs.Mob;
import game.entities.mobs.player.Player;
import game.map.Level;
import game.map.collision.WorldGrid;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import static java.util.logging.Level.SEVERE;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;
import messages.lobby.HostJoinedLobbyMessage;

public class ClientGameAppState extends AbstractAppState implements ClientStateListener {
    @Getter
    private final AppStateManager stateManager;

    @Getter
    @Setter
    private static ClientGameAppState instance;

    private final AppSettings applicationSettings;

    @Getter
    private Client client;

    @Getter
    @Setter
    private ActionListener actionListener;

    @Getter
    @Setter
    private AnalogListener analogListener;

    @Getter
    @Setter
    private Nifty nifty;

    @Getter
    private final String serverIp;

    @Getter
    private boolean debug;

    @Setter
    @Getter
    private ClientGameManager currentGamemode = new ClientStoryGameManager();

    @Getter
    private MenuStateMachine menuStateMachine;

    private Picture crosshair;

    @Getter
    @Setter
    private InputController inputController;

    private Node guiNode = new Node("Client game gui node");

    public ClientGameAppState(Main app, String serverIp) {
        instance = this;
        this.applicationSettings = app.getAppSettings();
        stateManager = Main.getInstance().getStateManager();
        this.serverIp = serverIp;
        app.getGuiNode().attachChild(guiNode);
        this.menuStateMachine = new MenuStateMachine(guiNode, applicationSettings.getWidth(),applicationSettings.getHeight());
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {

        currentGamemode.startGame();
        connectToServer();
        currentGamemode.getLevelManager().setClient(client);
    }

    public void joinGame() {
        drawCrosshair();
    }

    @Override
    public void update(float tpf) {
        if (currentGamemode != null) {
            currentGamemode.updateMainLoop(tpf);
        }

    }

    public <T extends Entity> T registerEntity(T entity) {
        return currentGamemode.getLevelManager().registerEntity(entity);
    }

    public void setPlayer(Player player) {
        currentGamemode.getLevelManager().setPlayer(player);
    }

    public Player registerPlayer(Integer id, boolean setAsPlayer, int playerClassIndex) {
        return currentGamemode.getLevelManager().registerPlayer(id, setAsPlayer, playerClassIndex);
    }

    public Mob registerMob(Integer id, MobSpawnType spawnType) {
        return currentGamemode.getLevelManager().registerMob(id, spawnType);
    }

    public int getBLOCK_SIZE() {
        return currentGamemode.getLevelManager().getBLOCK_SIZE();
    }

    public ConcurrentHashMap<Integer, Entity> getMobs() {
        return currentGamemode.getLevelManager().getMobs();
    }

    public Level getMap() {
        return currentGamemode.getLevelManager().getLevel();
    }

    public InputManager getInputManager() {
        return currentGamemode.getLevelManager().getInputManager();
    }

    public AssetManager getAssetManager() {
        return currentGamemode.getLevelManager().getAssetManager();
    }

    public RenderManager getRenderManager() {
        return currentGamemode.getLevelManager().getRenderManager();
    }

    public Node getEntityNode() {
        return currentGamemode.getLevelManager().getEntityNode();
    }

    public Node getDestructibleNode() {
        return currentGamemode.getLevelManager().getDestructibleNode();
    }

    public Node getPickableNode() {
        return currentGamemode.getLevelManager().getPickableNode();
    }

    public Node getDebugNode() {
        return currentGamemode.getLevelManager().getDebugNode();
    }

    public Node getMapNode() {
        return currentGamemode.getLevelManager().getMapNode();
    }

    public Player getPlayer() {
        return currentGamemode.getLevelManager().getPlayer();
    }

    public WorldGrid getGrid() {
        return currentGamemode.getLevelManager().getGrid();
    }

    public Camera getCamera() {
        return Main.getInstance().getCamera();
    }

    public FlyByCamera getFlyCam() {
        return currentGamemode.getLevelManager().getFlyCam();
    }

    public AppSettings getSettings() {
        return applicationSettings;
    }

    public static void removeEntityByIdClient(int id) {
        instance.currentGamemode.getLevelManager().getMobs().remove(id);
    }

    public void connectToServer() {
        try {
            client = Network.connectToServer(serverIp, NetworkingInitialization.PORT);
            client.addClientStateListener(this);
            client.start();

        } catch (IOException ex) {
            Logger.getLogger(ClientGameAppState.class.getName()).log(SEVERE, null, ex);
        }
    }

    @Override
    public void clientConnected(Client client) {
        client.addMessageListener(new ClientMessageListener(this));
        var msg = new HostJoinedLobbyMessage(client.getId(), "Player " + client.getId());
        client.send(msg);
    }

    @Override
    public void clientDisconnected(Client client, DisconnectInfo di) {

    }

    public void drawCrosshair() {
        crosshair = new Picture("crosshair");
        crosshair.setImage(Main.getInstance().getAssetManager(), "Textures/GUI/crosshair.png", true);
        crosshair.setWidth(getSettings().getHeight() * 0.04f);
        crosshair.setHeight(getSettings().getHeight() * 0.04f); //0.04f
        crosshair.setPosition((getSettings().getWidth() / 2) - getSettings().getHeight() * 0.04f / 2, getSettings().getHeight() / 2 - getSettings().getHeight() * 0.04f / 2);
        Main.getInstance().getGuiNode().attachChild(crosshair);
    }

    @Override
    public void stateDetached(AppStateManager stateManager) {
        super.stateDetached(stateManager);

        if (ClientGameAppState.getInstance().getClient().isStarted()) {
            ClientGameAppState.getInstance().getClient().close();
        }

        var csr = ClientGameAppState.getInstance().getClient().getServices().getService(ClientSerializerRegistrationsService.class);
        if(csr != null) {
            ClientGameAppState.getInstance().getClient().getServices().removeService(csr);
        }

        var hudState = stateManager.getState(PlayerHUD.class);
        stateManager.detach(hudState);
        crosshair.removeFromParent();
        currentGamemode.getLevelManager().cleanup();
        ClientGameAppState.setInstance(null);
    }
}
