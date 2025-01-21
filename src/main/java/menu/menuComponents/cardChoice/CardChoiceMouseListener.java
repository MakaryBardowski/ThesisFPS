package menu.menuComponents.cardChoice;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.simsilica.lemur.event.MouseListener;

import static settings.GlobalSettings.CARD_PICK_MOUSE_BUTTON;

public class CardChoiceMouseListener implements MouseListener {
    private static final float CARD_SIZE_INCREASE_ON_HOVER_PERCENT = 0.1f;

    private final CardChoiceComponent cardChoiceComponent;
    private final Vector3f initialPosition;

    private boolean pressedThisCard = false;
    private boolean hoveringOverThisCard = false;

    public CardChoiceMouseListener(CardChoiceComponent cardChoiceComponent){
        this.cardChoiceComponent = cardChoiceComponent;
        this.initialPosition = cardChoiceComponent.getLocalTranslation().clone();
    }

    @Override
    public void mouseButtonEvent(MouseButtonEvent event, Spatial target, Spatial capture) {
        event.setConsumed();
        if(!pressedThisCard && event.isPressed() && event.getButtonIndex() == CARD_PICK_MOUSE_BUTTON){
            pressedThisCard = true;
        }

        if(hoveringOverThisCard && pressedThisCard && event.isReleased() && event.getButtonIndex() == CARD_PICK_MOUSE_BUTTON){
            cardChoiceComponent.pick();
        }
    }

    @Override
    public void mouseEntered(MouseMotionEvent event, Spatial target, Spatial capture) {
        hoveringOverThisCard = true;
        pressedThisCard = false;
        cardChoiceComponent.scaleRelativeToInitialSize(1+CARD_SIZE_INCREASE_ON_HOVER_PERCENT);
    }

    @Override
    public void mouseExited(MouseMotionEvent event, Spatial target, Spatial capture) {
        hoveringOverThisCard = false;
        pressedThisCard = false;
        cardChoiceComponent.scaleRelativeToInitialSize(1);
    }

    @Override
    public void mouseMoved(MouseMotionEvent event, Spatial target, Spatial capture) {

    }
}
