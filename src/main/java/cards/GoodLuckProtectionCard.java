package cards;

import game.entities.StatusEffectContainer;
import server.ServerGameAppState;
import statusEffects.EffectTemplates;

public class GoodLuckProtectionCard extends AugmentCardTemplate {

    public GoodLuckProtectionCard() {
        super(3,"Good Luck\nProtection", "All damage you deal has \na random multiplier of \n\\#FF0000#85\\#FFFFFF#%-\\#FF0000#120\\#FFFFFF#%.","Textures/GUI/Cards/cutdown.png");
    }

    @Override
    public void chooseCardClient(StatusEffectContainer statusEffectContainer) {
        System.out.println(statusEffectContainer + " chose "+this.getName());
    }

    @Override
    public void chooseCardServer(StatusEffectContainer statusEffectContainer) {
        var effect = ServerGameAppState.getInstance().getCurrentGamemode().getLevelManager().createAndRegisterStatusEffect(EffectTemplates.GOOD_LUCK_PROTECTION,statusEffectContainer);
        statusEffectContainer.addEffect(effect);
    }
}
