package server;

import messages.gameSetupMessages.GameInfoOnStartMessage;

public class ServerStoryGameManager extends ServerGameManager {

    private final int LEVEL_COUNT = 8;

    public ServerStoryGameManager() {
        gamemodeId = 1;
    }

    @Override
    public void startGame() {
        setupLevelManager();
        notifySeedsToConnectedClients();
        levelManager.jumpToLevel(0);
    }

    @Override
    public void updateMainLoop(float tpf) {}

    private void notifySeedsToConnectedClients() {
        var server = ServerMain.getInstance().getServer();
        var levelSeeds = levelManager.getLevelSeeds();
        var levelTypes = levelManager.getLevelTypes();
        var levelSeedsMessage = new GameInfoOnStartMessage(gamemodeId, levelSeeds,levelTypes);
        server.broadcast(levelSeedsMessage);
    }

    private void setupLevelManager() {
        levelManager = new ServerLevelManager(LEVEL_COUNT, ServerMain.getInstance().getServer());
        levelManager.setupLevelSeeds();
    }

}
