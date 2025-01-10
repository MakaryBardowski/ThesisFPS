package cards;

import java.util.HashMap;
import java.util.Map;


public class AugmentCardsTemplateRegistry {
    private static final Map<Integer, AugmentCardTemplate> CARD_REGISTRY = new HashMap<>();

    static {
        var cutDown = new CutDownCard();
        var pinpoint = new PinpointCard();

        CARD_REGISTRY.put(cutDown.getCardId(), cutDown);
        CARD_REGISTRY.put(pinpoint.getCardId(), pinpoint);
    }

    public static AugmentCardTemplate getCardById(Integer id){
        return CARD_REGISTRY.get(id);
    }
}
