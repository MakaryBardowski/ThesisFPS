package cards;

import game.entities.StatusEffectContainer;
import server.ServerMain;
import statusEffects.EffectTemplates;

public class CutDownCard extends AugmentCardTemplate {

    public CutDownCard() {
        super(0,"Cut Down", "Dealing damage to targets with \nless than \\#FF0000#15\\#FFFFFF#% health instantly kills them.","Textures/GUI/Cards/cutdown.png");
    }

    @Override
    public void chooseCardClient(StatusEffectContainer statusEffectContainer) {
        System.out.println(statusEffectContainer + " chose "+this.getName());
    }

    @Override
    public void chooseCardServer(StatusEffectContainer statusEffectContainer) {
        var effect = ServerMain.getInstance().getCurrentGamemode().getLevelManager().createAndRegisterStatusEffect(EffectTemplates.CUTDOWN_CARD_EXECUTE,statusEffectContainer);
        statusEffectContainer.addEffect(effect);
    }
}
