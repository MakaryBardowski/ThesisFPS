package server;

import lombok.Getter;

public abstract class ServerGameManager implements GameManagerInterface{
    protected int gamemodeId;
    
    @Getter
    protected ServerLevelManager levelManager;

}
