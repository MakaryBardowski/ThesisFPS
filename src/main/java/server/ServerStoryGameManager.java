package server;

import data.jumpToLevelData.BaseJumpToLevelData;

public class ServerStoryGameManager extends ServerGameManager {

    public static final int LEVEL_COUNT = 13;

    public ServerStoryGameManager() {
        gamemodeId = 1;
    }

    @Override
    public void startGame() {
        setupLevelManager();
        levelManager.jumpToLevel(new BaseJumpToLevelData(0,levelManager.getLevelSeeds()[0],levelManager.getLevelTypes()[0]));
    }

    @Override
    public void updateMainLoop(float tpf) {}

    private void setupLevelManager() {
        levelManager = new ServerLevelManager(LEVEL_COUNT, ServerGameAppState.getInstance().getServer());
        levelManager.setupLevelSeeds();
    }

}
