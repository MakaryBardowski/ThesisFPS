package client;

public class ClientStoryGameManager extends ClientGameManager {
    
    @Override
    public void startGame() {
        setupLevelManager();
        initializeLevelManager();
    }

    @Override
    public void updateMainLoop(float tpf) {
        if (levelManager != null) {
            levelManager.updateLoop(tpf);
        }
    }

    private void setupLevelManager() {
        levelManager = new ClientLevelManager();
    }

    private void initializeLevelManager() {
        levelManager.initialize();
    }

}
