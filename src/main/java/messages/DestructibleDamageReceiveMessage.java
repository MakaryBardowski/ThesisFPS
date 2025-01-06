package messages;

import client.ClientGameAppState;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import data.DamageReceiveData;
import game.entities.Destructible;
import game.entities.InteractiveEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import server.ServerMain;

@Serializable
@Getter
@NoArgsConstructor
public class DestructibleDamageReceiveMessage extends EntityUpdateMessage {

    protected float damage;
    protected int attackerId;
    
    public DestructibleDamageReceiveMessage(int targetId,int attackerId, float damage) {
        super(targetId);
        this.damage = damage;
        this.attackerId = attackerId;
    }
    
    public DamageReceiveData getDamageReceiveData() {
        return new DamageReceiveData(id,attackerId,damage);
    }

    @Override
    public void handleServer(ServerMain server,HostedConnection hc) {
        InteractiveEntity i = getEntityByIdServer(id);
        if (i != null) { // if the mob doesnt exist, it means the 
            // info was sent from a lagged user - dont forward it to others
//            System.err.println(id);
            Destructible d = ((Destructible) i);
            applyDestructibleDamageAndNotifyClients(d, ServerMain.getInstance());

        }
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        enqueueExecution(() -> {
            if (entityExistsLocallyClient(id)) {
                Destructible d = (Destructible) getEntityByIdClient(id);
                d.receiveDamage(getDamageReceiveData());
            }
        }
        );
    }

    public void applyDestructibleDamageAndNotifyClients(Destructible d, ServerMain serverApp) {
            d.receiveDamageServer(getDamageReceiveData());
            this.setReliable(true);
            serverApp.getServer().broadcast(this);
    }
}