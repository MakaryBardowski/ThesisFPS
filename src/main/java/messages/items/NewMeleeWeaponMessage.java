package messages.items;

import client.appStates.ClientGameAppState;
import client.Main;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import game.items.weapons.MeleeWeapon;
import lombok.Getter;
import server.ServerGameAppState;

@Serializable
@Getter
public class NewMeleeWeaponMessage extends NewItemMessage {
    private float damage;

    public NewMeleeWeaponMessage() {
    }

    public NewMeleeWeaponMessage(MeleeWeapon item) {
        super(item);
        this.damage = item.getDamage();
    }

    @Override
    public void handleServer(ServerGameAppState server, HostedConnection hc) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void handleClient(ClientGameAppState client) {
            var i = (MeleeWeapon) ifa.createItem(id, getTemplate(), droppable);
            i.setName(name);
            i.setDamage(damage);
            if(isAlreadyDropped()){
                Main.getInstance().enqueue(()-> {
                    i.drop(getDroppedPosition());
                });
            }
            client.registerEntity(i);
    }
}
