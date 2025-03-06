package cards;

import game.entities.StatusEffectContainer;
import server.ServerGameAppState;
import statusEffects.EffectTemplates;

public class OverfedCard extends AugmentCardTemplate {

    public OverfedCard() {
        super(4,"Overfed", "Killing targets permanently \ngrants you \\#FF0000#0.5\\#FFFFFF# \nmaximum health.","Textures/GUI/Cards/regeneration.png");
    }

    @Override
    public void chooseCardClient(StatusEffectContainer statusEffectContainer) {
        System.out.println(statusEffectContainer + " chose "+this.getName());
    }

    @Override
    public void chooseCardServer(StatusEffectContainer statusEffectContainer) {
        var effect = ServerGameAppState.getInstance().getCurrentGamemode().getLevelManager().createAndRegisterStatusEffect(EffectTemplates.OVERFED_HEALTH_STACK,statusEffectContainer);
        statusEffectContainer.addEffect(effect);
    }
}
