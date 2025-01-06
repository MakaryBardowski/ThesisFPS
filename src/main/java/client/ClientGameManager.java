package client;
import lombok.Getter;
import server.GameManagerInterface;

public abstract class ClientGameManager implements GameManagerInterface {
    @Getter
    protected ClientLevelManager levelManager;
}
