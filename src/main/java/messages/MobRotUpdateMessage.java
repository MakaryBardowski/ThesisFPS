package messages;

import client.appStates.ClientGameAppState;
import client.Main;
import com.jme3.math.Quaternion;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import game.entities.AttachedEntity;
import game.entities.Movable;
import server.ServerGameAppState;

@Serializable
public class MobRotUpdateMessage extends EntityUpdateMessage {

    private float w;
    private float x;
    private float y;
    private float z;

    public MobRotUpdateMessage() {
    }

    public MobRotUpdateMessage(int id, Quaternion rot) {
        super(id);
        this.w = rot.getW();
        this.x = rot.getX();
        this.y = rot.getY();
        this.z = rot.getZ();
    }

    public Quaternion getRot() {
        return new Quaternion(x, y, z, w);
    }

    @Override
    public String toString() {
        return "MobUpdatePosRotMessage{ id=" + id + " ,rot=" + getRot() + '}';
    }

    @Override
    public void handleServer(ServerGameAppState server, HostedConnection hc) {
        if (entityExistsLocallyServer(id) ) {
            Main.getInstance().enqueue(() -> {
                var entity = (AttachedEntity) ServerGameAppState.getInstance().getLevelManagerMobs().get(id);
                if (entity != null) {
                    entity.getNode().setLocalRotation(getRot());
                    if (entity instanceof Movable movable) {
                        movable.getPositionChangedOnServer().set(true); // we set to true so both position AND ROTATION are broadcast
                    }

                }
            });
        }
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        if (entityExistsLocallyClient(id)) {
            getMobByIdClient(id).setServerRotation(getRot());
        }
    }

}
