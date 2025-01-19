package server;

import data.jumpToLevelData.BaseJumpToLevelData;

public class ServerStoryGameManager extends ServerGameManager {

    private static final int LEVEL_COUNT = 7;

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
        levelManager = new ServerLevelManager(LEVEL_COUNT, ServerMain.getInstance().getServer());
        levelManager.setupLevelSeeds();
    }

}
