package messages;

import client.appStates.ClientGameAppState;
import client.Main;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import game.entities.AttachedEntity;
import server.ServerGameAppState;

@Serializable
public class MobPosRotUpdateMessage extends EntityUpdateMessage {

    protected float x;
    protected float y;
    protected float z;

    private float qw;
    private float qx;
    private float qy;
    private float qz;

    public MobPosRotUpdateMessage() {
    }

    public MobPosRotUpdateMessage(int id, Vector3f pos, Quaternion rot) {
        super(id);
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();

        this.qw = rot.getW();
        this.qx = rot.getX();
        this.qy = rot.getY();
        this.qz = rot.getZ();
    }

    public Vector3f getPos() {
        return new Vector3f(x, y, z);
    }

    public void setPos(Vector3f pos) {
        this.x = pos.x;
        this.y = pos.y;
        this.z = pos.z;
    }

    public Quaternion getRot() {
        return new Quaternion(qx, qy, qz, qw);
    }

    public void setRot(Quaternion quat) {
        this.qw = quat.getW();
        this.qx = quat.getX();
        this.qy = quat.getY();
        this.qz = quat.getZ();
    }

    @Override
    public String toString() {
        return "MobUpdatePosRotMessage{ id=" + id + " x=" + x + ", y=" + y + ", z=" + z + '}';
    }

    @Override
    public void handleServer(ServerGameAppState server, HostedConnection hc) {
        if (entityExistsLocallyServer(id)) {
            Main.getInstance().enqueue(() -> {
                if (ServerGameAppState.getInstance().getLevelManagerMobs().get(id) != null) {
                    ((AttachedEntity)  ServerGameAppState.getInstance().getLevelManagerMobs().get(id)) .getNode().setLocalRotation(getRot());
                }
            });
        }
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        if (entityExistsLocallyClient(id)) {
            getMobByIdClient(id).setServerLocation(getPos());
            getMobByIdClient(id).setServerRotation(getRot());
        }
    }

}
