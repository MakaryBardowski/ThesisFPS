package messages.items;

import client.appStates.ClientGameAppState;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import game.entities.mobs.HumanMob;
import game.items.Item;
import game.items.armor.Boots;
import game.items.armor.Gloves;
import game.items.armor.Helmet;
import game.items.armor.Vest;
import lombok.Getter;
import messages.TwoWayMessage;
import server.ServerGameAppState;

@Serializable
public class SetDefaultItemMessage extends TwoWayMessage {

    @Getter
    protected int itemId;

    @Getter
    protected int mobId;

    protected int interactionTypeIndex;

    public SetDefaultItemMessage() {
    }

    public SetDefaultItemMessage(Item item, HumanMob mob) {
        this.itemId = item.getId();
        this.mobId = mob.getId();
    }

    @Override
    public void handleServer(ServerGameAppState server, HostedConnection hc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        setHumanMobDefaultItem(this);
    }

    private void setHumanMobDefaultItem(SetDefaultItemMessage dmsg) {
        enqueueExecution(() -> {
            HumanMob human = (HumanMob) getMobByIdClient(dmsg.getMobId());
            Item item = getItemByIdClient(dmsg.getItemId());
            if (item instanceof Vest v) {
                human.setDefaultVest(v);
            } else if (item instanceof Helmet h) {
                human.setDefaultHelmet(h);
            } else if (item instanceof Gloves g) {
                human.setDefaultGloves(g);
            } else if (item instanceof Boots b) {
                human.setDefaultBoots(b);
            }
        });

    }

}
