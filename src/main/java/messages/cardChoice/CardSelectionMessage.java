package messages.cardChoice;

import client.appStates.ClientGameAppState;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import lombok.Getter;
import menu.states.CardChoiceMenuState;
import messages.TwoWayMessage;
import server.ServerGameAppState;

@Getter
@Serializable
public class CardSelectionMessage extends TwoWayMessage {
    private int cardSessionId;
    private int cardId1;
    private int cardId2;
    private int cardId3;

    public CardSelectionMessage(){
        setReliable(true);
    }

    public CardSelectionMessage(int cardSessionId, int cardId1, int cardId2, int cardId3){
        this.cardSessionId = cardSessionId;
        this.cardId1 = cardId1;
        this.cardId2 = cardId2;
        this.cardId3 = cardId3;
        this.setReliable(true);
    }

    @Override
    public void handleServer(ServerGameAppState server, HostedConnection sender) {
        throw new UnsupportedOperationException("Only server can decide card choice screen!");
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        enqueueExecution(()->{
            client.getMenuStateMachine().forceState(new CardChoiceMenuState(cardSessionId,cardId1,cardId2,cardId3, client.getPlayer().getId(), client.getClient()));
        });
    }
}
