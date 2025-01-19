package messages;

import client.appStates.ClientGameAppState;
import com.jme3.network.Filters;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import game.entities.Animated;
import game.entities.Animation;
import server.ServerMain;

@Serializable
public class AnimationPlayedMessage extends EntityUpdateMessage {

    private int animationOrdinal = -1;

    public AnimationPlayedMessage() {}

    public AnimationPlayedMessage(int id, Animation animation) {
        super(id);
        animationOrdinal = animation.ordinal();
    }
    
    public Animation getAnimation(){
        return Animation.values()[animationOrdinal];
    }


    @Override
    public void handleServer(ServerMain server,HostedConnection hc) {
        server.getServer().broadcast(Filters.notEqualTo(hc),this);
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        var entity = getEntityByIdClient(id);
        
        if(entity == null){
        return;
        }
        
        if(entity instanceof Animated animatedEntity){
            animatedEntity.playAnimation(getAnimation());
        }
    }

}
