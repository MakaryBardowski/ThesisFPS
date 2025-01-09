package menu.states;

import cards.AugmentCardTemplate;
import cards.AugmentCardsTemplateRegistry;
import com.jme3.math.Vector3f;
import com.jme3.network.Client;
import com.jme3.scene.Node;
import com.simsilica.lemur.GuiGlobals;
import game.entities.mobs.player.Player;
import guiComponents.menuComponents.CardChoiceComponent;
import messages.cardChoice.ChooseCardMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CardChoiceMenuState implements MenuState {
    private List<CardChoiceComponent> cards = new ArrayList<>(3);

    private final int cardId1;
    private final int cardId2;
    private final int cardId3;
    private final int playerId;
    private final Client client;

    public CardChoiceMenuState(int cardId1, int cardId2, int cardId3, int playerId, Client client){
        this.cardId1 = cardId1;
        this.cardId2 = cardId2;
        this.cardId3 = cardId3;
        this.client = client;
        this.playerId = playerId;
    }

    @Override
    public void close() {
        Player.enablePlayerControls();
        GuiGlobals.getInstance().setCursorEventsEnabled(false,true);

        for(var card : cards){
            card.removeFromParent();
        }
    }

    @Override
    public void open(Node guiNode,float resolutionX, float resolutionY) {
        Player.disablePlayerControls();
        GuiGlobals.getInstance().setCursorEventsEnabled(true,true);

        float cardMarginSide = resolutionX*0.15f;
        float cardMarginBot = resolutionY*0.8f;

        float cardSizeX = resolutionX*0.2f;
        float cardSizeY = resolutionY*0.6f;

        Consumer<AugmentCardTemplate> onCardChoose = (card) -> {
            client.send(new ChooseCardMessage(playerId,card.getCardId()));
//            card.chooseCardClient();
        };

        var cardPos1 = new Vector3f(cardMarginSide,cardMarginBot,0);
        var cardPos2 = new Vector3f(cardMarginSide + cardSizeX + 0.0333333333f*resolutionX ,cardMarginBot,0);
        var cardPos3 = new Vector3f(cardMarginSide + cardSizeX*2 + 0.0333333333f*resolutionX*2,cardMarginBot,0);

        var card1 = new CardChoiceComponent(AugmentCardsTemplateRegistry.getCardById(cardId1),cardSizeX, cardSizeY, cardPos1, onCardChoose);
        var card2 = new CardChoiceComponent(AugmentCardsTemplateRegistry.getCardById(cardId2), cardSizeX,cardSizeY, cardPos2,onCardChoose);
        var card3 = new CardChoiceComponent(AugmentCardsTemplateRegistry.getCardById(cardId3), cardSizeX,cardSizeY, cardPos3,onCardChoose);

        cards.add(card1);
        cards.add(card2);
        cards.add(card3);

        guiNode.attachChild(card1);
        guiNode.attachChild(card2);
        guiNode.attachChild(card3);
    }

    @Override
    public boolean isTransitionAllowed(MenuState newState) {
        return false;
    }

}
