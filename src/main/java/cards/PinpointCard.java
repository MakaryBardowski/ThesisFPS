package cards;

import game.entities.StatusEffectContainer;
import server.ServerMain;
import statusEffects.EffectTemplates;

public class PinpointCard extends AugmentCardTemplate {

    public PinpointCard() {
        super(1,"Pinpoint", "Gain \\#FF0000#3\\#FFFFFF#% critical hit chance, increased by \\#FF0000#1\\#FFFFFF#% for each tile between you and your target. Critical hits deal \\#FF0000#200\\#FFFFFF#% damage.","Textures/GUI/Cards/pinpoint.png");
    }

    @Override
    public void chooseCardClient(StatusEffectContainer statusEffectContainer) {
        System.out.println(statusEffectContainer + " chose "+this.getName());
    }

    @Override
    public void chooseCardServer(StatusEffectContainer statusEffectContainer) {
        var effect = ServerMain.getInstance().getCurrentGamemode().getLevelManager().createAndRegisterStatusEffect(EffectTemplates.DEADEYE_CARD_CRIT,statusEffectContainer);
        statusEffectContainer.addEffect(effect);
    }
}
