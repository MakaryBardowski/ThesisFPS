package cards;

import game.entities.StatusEffectContainer;
import server.ServerGameAppState;
import statusEffects.EffectTemplates;

public class HardnessCard extends AugmentCardTemplate {

    public HardnessCard() {
        super(2,"Hardness", "Reduce all incoming damage by\n\\#FF0000#1\\#FFFFFF#.","Textures/GUI/Cards/regeneration.png");
    }

    @Override
    public void chooseCardClient(StatusEffectContainer statusEffectContainer) {
        System.out.println(statusEffectContainer + " chose "+this.getName());
    }

    @Override
    public void chooseCardServer(StatusEffectContainer statusEffectContainer) {
        var effect = ServerGameAppState.getInstance().getCurrentGamemode().getLevelManager().createAndRegisterStatusEffect(EffectTemplates.HARDNESS_CARD_ARMOR,statusEffectContainer);
        statusEffectContainer.addEffect(effect);
    }
}
