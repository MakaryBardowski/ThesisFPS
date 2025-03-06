package messages;

import client.appStates.ClientGameAppState;
import client.Main;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import game.entities.Chest;
import game.entities.Destructible;
import game.entities.Entity;
import game.entities.mobs.Mob;
import game.entities.mobs.player.Player;
import game.items.Item;
import server.ServerGameAppState;

@Serializable
public abstract class TwoWayMessage extends AbstractMessage {

    public abstract void handleServer(ServerGameAppState server, HostedConnection sender);

    public abstract void handleClient(ClientGameAppState client);

    protected Chest getChestByIdClient(int id) {
        return ((Chest) ClientGameAppState.getInstance().getMobs().get(id));
    }

    protected Chest getChestByIdServer(int id) {
        return ((Chest) ServerGameAppState.getInstance().getLevelManagerMobs().get(id));
    }

    protected Entity getEntityByIdServer(int id) {
        return ServerGameAppState.getInstance().getLevelManagerMobs().get(id);
    }

    protected Mob getMobByIdServer(int id) {
        return ((Mob) ServerGameAppState.getInstance().getLevelManagerMobs().get(id));
    }

    protected Item getItemByIdServer(int id) {
        return ((Item) ServerGameAppState.getInstance().getLevelManagerMobs().get(id));
    }

    protected Destructible getDestructibleByIdServer(int id) {
        return ((Destructible) ServerGameAppState.getInstance().getLevelManagerMobs().get(id));
    }

    protected Entity getEntityByIdClient(int id) {
        return ClientGameAppState.getInstance().getMobs().get(id);
    }

    protected Mob getMobByIdClient(int id) {
        return ((Mob) ClientGameAppState.getInstance().getMobs().get(id));
    }

    protected Item getItemByIdClient(int id) {
        return ((Item) ClientGameAppState.getInstance().getMobs().get(id));
    }

    protected Destructible getDestructibleByIdClient(int id) {
        return ((Destructible) ClientGameAppState.getInstance().getMobs().get(id));
    }

    protected void removeItemFromMobEquipmentClient(int mobId, int itemId) {
        var mob = getMobByIdClient(mobId);
        var item = getItemByIdClient(itemId);
        var mobEquipment = mob.getEquipment();
        mobEquipment.removeItem(item);
    }

    protected boolean entityExistsLocallyClient(int mobId) {
        return ClientGameAppState.getInstance().getMobs().get(mobId) != null;
    }

    protected boolean entityExistsLocallyServer(int mobId) {
        return  null != ServerGameAppState.getInstance().getLevelManagerMobs().get(mobId);
    }

    protected boolean entityNotExistsLocallyClient(int mobId) {
        return ClientGameAppState.getInstance().getMobs().get(mobId) == null;
    }

    protected boolean entityNotExistsLocallyServer(int mobId) {
        return ServerGameAppState.getInstance().getLevelManagerMobs().get(mobId) == null;
    }

    protected HostedConnection getHostedConnectionByPlayer(Player p) {
        return ServerGameAppState.getInstance().getHostsByPlayerId().get(p.getId());
    }

    protected void placeMob(Vector3f pos, Mob p) {
        ClientGameAppState.getInstance().getDestructibleNode().attachChild(p.getNode());
        p.getNode().setLocalTranslation(pos);
        ClientGameAppState.getInstance().getGrid().insert(p);
    }

    protected void enqueueExecution(Runnable runnable) {
        Main.getInstance().enqueue(runnable);
    }
}
