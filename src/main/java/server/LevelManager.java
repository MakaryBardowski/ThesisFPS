package server;

import game.map.MapType;
import lombok.Getter;
import lombok.Setter;

public abstract class LevelManager {

    @Getter
    @Setter
    protected long[] levelSeeds;

    @Getter
    @Setter
    protected MapType[] levelTypes;
    
    @Getter
    protected int currentLevelIndex;

    public abstract void jumpToLevel(int levelIndex);
    }
