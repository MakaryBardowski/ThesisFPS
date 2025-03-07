package messages;

import client.appStates.ClientGameAppState;
import com.jme3.math.Vector3f;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import game.entities.DestructibleUtils;
import game.entities.factories.MobSpawnType;
import game.entities.mobs.Mob;
import game.map.blocks.VoxelLighting;
import lombok.Getter;
import server.ServerGameAppState;

@Serializable
public class NewMobMessage extends TwoWayMessage {

    @Getter
    private MobSpawnType mobType;
    @Getter
    private int id;
    @Getter
    private float health;
    @Getter
    private float maxHealth;
    private float x;
    private float y;
    private float z;
    private float speed;

    public NewMobMessage() {
    }

    public NewMobMessage(Mob mob, Vector3f pos, MobSpawnType mobType) {
        this.id = mob.getId();
        this.mobType = mobType;
        this.health = mob.getHealth();
        this.maxHealth = mob.getMaxHealth();
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.speed = mob.getCachedSpeed();
    }

    @Override
    public void handleServer(ServerGameAppState server, HostedConnection hc) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        addMob(this);
    }

    public Vector3f getPos() {
        return new Vector3f(x, y, z);
    }

    private void addMob(NewMobMessage nmsg) {
        if (entityNotExistsLocallyClient(nmsg.getId())) {
            enqueueExecution(() -> {

                Mob p = ClientGameAppState.getInstance().registerMob(nmsg.getId(), nmsg.getMobType());
                VoxelLighting.setupModelLight(p.getNode());
                DestructibleUtils.setupModelShootability(p.getNode(), p.getId());
                placeMob(nmsg.getPos(), p);
                p.setMaxHealth(nmsg.getMaxHealth());
                p.setHealth(nmsg.getHealth());
                p.setCachedSpeed(speed);
            }
            );
        }
    }

}
