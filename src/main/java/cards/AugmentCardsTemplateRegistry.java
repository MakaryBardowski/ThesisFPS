package cards;

import java.util.HashMap;
import java.util.Map;


public class AugmentCardsTemplateRegistry {
    private static final Map<Integer, AugmentCardTemplate> CARD_REGISTRY = new HashMap<>();

    static {
        var cutDown = new CutDownCard();
        var pinpoint = new PinpointCard();
        var hardness = new HardnessCard();
        var goodLuckProtection = new GoodLuckProtectionCard();
        var overfed = new OverfedCard();
        CARD_REGISTRY.put(cutDown.getCardId(), cutDown);
        CARD_REGISTRY.put(pinpoint.getCardId(), pinpoint);
        CARD_REGISTRY.put(hardness.getCardId(),hardness);
        CARD_REGISTRY.put(goodLuckProtection.getCardId(),goodLuckProtection);
        CARD_REGISTRY.put(overfed.getCardId(),overfed);
    }

    public static AugmentCardTemplate getCardById(Integer id){
        return CARD_REGISTRY.get(id);
    }

    public static int getSize(){
        return CARD_REGISTRY.size();
    }
}
