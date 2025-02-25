package messages;

import client.appStates.ClientGameAppState;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import data.DamageReceiveData;
import game.entities.Destructible;
import game.entities.Entity;
import game.entities.mobs.Mob;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import server.ServerGameAppState;

@Serializable
@Getter
@NoArgsConstructor
public class DestructibleDamageReceiveMessage extends EntityUpdateMessage {
    @Setter
    protected float damage;
    protected int attackerId;
    
    public DestructibleDamageReceiveMessage(int targetId,int attackerId, float damage) {
        super(targetId);
        this.damage = damage;
        this.attackerId = attackerId;
    }

    public DestructibleDamageReceiveMessage(DamageReceiveData damageReceiveData) {
        super(damageReceiveData.getVictimId());
        this.damage = damageReceiveData.getRawDamage();
        this.attackerId = damageReceiveData.getAttackerId();
    }
    
    public DamageReceiveData getDamageReceiveData() {
        return new DamageReceiveData(id,attackerId,damage);
    }

    @Override
    public void handleServer(ServerGameAppState server, HostedConnection hc) {
        Entity victim = getEntityByIdServer(id);
        Entity attacker = getEntityByIdServer(attackerId);
        if (victim != null && attacker != null) { // if the mob doesnt exist, it means the
            // info was sent from a lagged user - dont forward it to others
            Mob attackerMob = ((Mob) attacker);
            Destructible victimMob = ((Destructible) victim);
            applyDestructibleDamageAndNotifyClients(victimMob,attackerMob, ServerGameAppState.getInstance());

        }
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        enqueueExecution(() -> {
            if (entityExistsLocallyClient(id)) {
                Destructible d = (Destructible) getEntityByIdClient(id);
                d.receiveDamageClient(getDamageReceiveData());
            }
        }
        );
    }

    public void applyDestructibleDamageAndNotifyClients(Destructible target, Mob attacker, ServerGameAppState serverApp) {
            enqueueExecution(()->{
                var damageReceiveData = getDamageReceiveData();
                attacker.dealDamageServer(damageReceiveData,target); // can be modified inside

                this.setDamage(damageReceiveData.getRawDamage());

                this.setReliable(true);
                serverApp.getServer().broadcast(this);
            });
    }
}
