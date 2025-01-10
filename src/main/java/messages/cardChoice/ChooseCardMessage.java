package messages.cardChoice;

import cards.AugmentCardsTemplateRegistry;
import client.ClientGameAppState;
import client.Main;
import com.jme3.network.HostedConnection;
import com.jme3.network.serializing.Serializable;
import game.entities.StatusEffectContainer;
import game.entities.mobs.player.Player;
import game.map.blocks.Map;
import lombok.Getter;
import messages.TwoWayMessage;
import server.ServerMain;

@Getter
@Serializable
public class ChooseCardMessage extends TwoWayMessage {
    private int cardChoiceSessionId;
    private int playerId;
    private int chosenCardId;

    public ChooseCardMessage(){}

    public ChooseCardMessage(int cardChoiceSessionId, int playerId, int chosenCardId){
        this.cardChoiceSessionId = cardChoiceSessionId;
        this.playerId = playerId;
        this.chosenCardId = chosenCardId;
        this.setReliable(true);
    }

    @Override
    public void handleServer(ServerMain server, HostedConnection sender) {
        if(entityNotExistsLocallyServer(playerId)) {
            System.err.println("[SERVER]" + playerId + " does not exist in registered mobs to choose a card!");
            return;
        }
            var mob = getEntityByIdServer(playerId);
            if(!(mob instanceof StatusEffectContainer player)){
                System.err.println("[SERVER]" + playerId + " is not a player, but wants to choose a card!");
                return;
            }
        var currentCardChoice = server.getCurrentGamemode().getLevelManager().getCardChoiceSessionsByIndex().get(cardChoiceSessionId);
        if(currentCardChoice.isPlayerAlreadyChosenCard(playerId)){
            System.err.println("[SERVER]" + playerId + " has already chosen a card!!");
            return;
        }

        var card = AugmentCardsTemplateRegistry.getCardById(chosenCardId);
        card.chooseCardServer(player);

        currentCardChoice.registerCardChosenByPlayer(playerId,card);

        server.getServer().broadcast(this);
    }

    @Override
    public void handleClient(ClientGameAppState client) {
        if(entityNotExistsLocallyClient(playerId)) {
            System.err.println("[CLIENT]" + playerId + " does not exist in registered mobs to choose a card!");
        }
        var mob = getEntityByIdClient(playerId);
        if(!(mob instanceof StatusEffectContainer player)){
            System.err.println("[CLIENT]" + playerId + " is not a player, but wants to choose a card! What was found is "+mob);
            return;
        }
        var card = AugmentCardsTemplateRegistry.getCardById(chosenCardId);
        card.chooseCardClient(player);

        if(playerId == client.getPlayer().getId()) {
            enqueueExecution(() -> {
                Main.getInstance().getMenuStateMachine().forceState(null);
            });
        }
    }
}
