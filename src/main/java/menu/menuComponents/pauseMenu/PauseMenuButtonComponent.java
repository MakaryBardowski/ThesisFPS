package menu.menuComponents.pauseMenu;

import client.Main;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.VAlignment;
import com.simsilica.lemur.component.QuadBackgroundComponent;

import static guiComponents.inventoryComponents.LemurUtils.getWidthRatio;

public class PauseMenuButtonComponent extends Container{
    private static final float INITIAL_BUTTON_LABEL_FONT_SIZE = 19f;

    private final Vector3f initialButtonSize;
    private final Vector3f initialButtonPos;
    private final Runnable onClick;

    private final Label buttonLabel;

    public PauseMenuButtonComponent(float buttonSizeX, float buttonSizeY, Vector3f cardPos, Runnable onClick, String label){
        this.onClick = onClick;

        buttonLabel = getButtonLabel(label);
        this.addChild(buttonLabel);
        var texture = Main.getInstance().getAssetManager().loadTexture("Textures/GUI/buttonBackground.png");

        var background = new QuadBackgroundComponent(texture);
        this.setBackground(background);
        initialButtonSize = new Vector3f(buttonSizeX,buttonSizeY,1);
        initialButtonPos = cardPos.clone();

        this.setPreferredSize(initialButtonSize.clone());

        this.setLocalTranslation(initialButtonPos.clone());
        this.addMouseListener(new PauseMenuButtonMouseListener(this));
    }

    private Label getButtonLabel(String label){
        var cardNameLabel = new Label("");
        var font = Main.getInstance().getAssetManager().loadFont("Interface/Fonts/pixelFlat11.fnt");
        cardNameLabel.setFont(font);
        cardNameLabel.setFontSize(INITIAL_BUTTON_LABEL_FONT_SIZE * getWidthRatio());
        cardNameLabel.setText(label);
        cardNameLabel.setTextHAlignment(HAlignment.Center);
        cardNameLabel.setTextVAlignment(VAlignment.Center);
        return cardNameLabel;
    }

    public void pick(){
        onClick.run();
    }

    public Spatial scaleRelativeToInitialSize(float newSize) {
        var sizeDifference = newSize - 1;
        var distanceToMove = sizeDifference/2;
        super.setPreferredSize(initialButtonSize.mult(newSize));
        buttonLabel.setFontSize(INITIAL_BUTTON_LABEL_FONT_SIZE *newSize*getWidthRatio());

        setLocalTranslation(
                initialButtonPos.subtract(initialButtonSize.getX()*distanceToMove, initialButtonSize.getY()*-distanceToMove, initialButtonSize.getZ())
        );
        return this;
    }
}
