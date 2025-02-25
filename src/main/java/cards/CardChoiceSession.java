package cards;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CardChoiceSession {
    @Getter
    private final int cardChoiceSessionId;
    private final Map<Integer,AugmentCardTemplate> cardsChosenByPlayerId = new ConcurrentHashMap<>();

    public CardChoiceSession(int cardChoiceSessionId){
        this.cardChoiceSessionId = cardChoiceSessionId;
    }

    public boolean isPlayerAlreadyChosenCard(int playerId){
        return cardsChosenByPlayerId.get(playerId) != null;
    }

    public AugmentCardTemplate getCardPickedByPlayer(int playerId){
        return cardsChosenByPlayerId.get(playerId);
    }

    public void registerCardChosenByPlayer(int id, AugmentCardTemplate card){
        cardsChosenByPlayerId.put(id,card);
    }
}
