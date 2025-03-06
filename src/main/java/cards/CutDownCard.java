package cards;

import game.entities.StatusEffectContainer;
import server.ServerGameAppState;
import statusEffects.EffectTemplates;

public class CutDownCard extends AugmentCardTemplate {

    public CutDownCard() {
        super(0,"Cut Down", "Dealing damage to targets with \nless than \\#FF0000#7.5\\#FFFFFF#% of your max \nhealth instantly kills them.","Textures/GUI/Cards/cutdown.png");
    }

    @Override
    public void chooseCardClient(StatusEffectContainer statusEffectContainer) {
        System.out.println(statusEffectContainer + " chose "+this.getName());
    }

    @Override
    public void chooseCardServer(StatusEffectContainer statusEffectContainer) {
        var effect = ServerGameAppState.getInstance().getCurrentGamemode().getLevelManager().createAndRegisterStatusEffect(EffectTemplates.CUTDOWN_CARD_EXECUTE,statusEffectContainer);
        statusEffectContainer.addEffect(effect);
    }
}
