package server;

import data.jumpToLevelData.BaseJumpToLevelData;
import game.map.MapType;
import lombok.Getter;
import lombok.Setter;

public abstract class LevelManager<T extends BaseJumpToLevelData>{
    @Getter
    protected int currentLevelIndex;

    public abstract void jumpToLevel(T jumpToLevelData);
    public abstract void cleanup();
}
