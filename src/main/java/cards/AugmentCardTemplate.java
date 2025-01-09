package cards;

import game.entities.StatusEffectContainer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class AugmentCardTemplate {

    private final int cardId;
    private final String name;
    private final String description;
    private final String iconPath;

    public abstract void chooseCardClient(StatusEffectContainer statusEffectContainer);
    public abstract void chooseCardServer(StatusEffectContainer statusEffectContainer);
}
