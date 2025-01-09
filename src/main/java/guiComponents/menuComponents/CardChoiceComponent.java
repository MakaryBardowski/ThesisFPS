package guiComponents.menuComponents;

import cards.AugmentCardTemplate;
import client.Main;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import com.simsilica.lemur.*;
import com.simsilica.lemur.component.*;

import java.util.function.Consumer;

import static guiComponents.inventoryComponents.LemurUtils.getWidthRatio;

public class CardChoiceComponent extends Container{
    private static final float INITIAL_CARD_NAME_FONT_SIZE = 19f;
    private static final float INITIAL_CARD_DESCRIPTION_FONT_SIZE = 11f;

    private final Vector3f initialCardSize;
    private final Vector3f initialCardPos;

    private final AugmentCardTemplate cardData;
    private final Consumer<AugmentCardTemplate> onCardChoose;

    private final Container titleAndIconContainer = new Container(new BoxLayout());
    private final Container descriptionContainer = new Container(new BoxLayout());
    private final Label cardDescriptionLabel;
    private final Label cardNameLabel;

    public CardChoiceComponent(AugmentCardTemplate cardData, float cardSizeX, float cardSizeY, Vector3f cardPos, Consumer<AugmentCardTemplate> onCardChoose){
        this.cardData = cardData;
        this.onCardChoose = onCardChoose;
        titleAndIconContainer.setInsetsComponent(new DynamicInsetsComponent(0.3f,0.3f,0.3f,0.3f));

        cardNameLabel = getAugmentNameLabel(cardData);
        cardDescriptionLabel = getCardDescription(cardData);
        titleAndIconContainer.addChild(cardNameLabel);
        titleAndIconContainer.addChild(getCardIcon(cardData,cardSizeX,cardSizeY));
        descriptionContainer.addChild(cardDescriptionLabel);
        this.addChild(titleAndIconContainer);
        this.addChild(descriptionContainer);
        var texture = Main.getInstance().getAssetManager().loadTexture("Textures/GUI/Cards/cardBackground.png");

        var background = new QuadBackgroundComponent(texture);
        this.setBackground(background);
        initialCardSize = new Vector3f(cardSizeX,cardSizeY,1);
        initialCardPos = cardPos.clone();

        this.setPreferredSize(initialCardSize.clone());

        this.setLocalTranslation(initialCardPos.clone());
        this.addMouseListener(new CardChoiceMouseListener(this));
    }

    private Label getAugmentNameLabel(AugmentCardTemplate cardData){
        var cardNameLabel = new Label("");
        var font = Main.getInstance().getAssetManager().loadFont("Interface/Fonts/pixelFlat11.fnt");
        cardNameLabel.setFont(font);
        cardNameLabel.setFontSize(INITIAL_CARD_NAME_FONT_SIZE * getWidthRatio());
        cardNameLabel.setText("\\#a80000#"+cardData.getName());
        cardNameLabel.setTextHAlignment(HAlignment.Center);
//        cardNameLabel.setTextVAlignment(VAlignment.Top);
        return cardNameLabel;
    }

    private Label getCardIcon(AugmentCardTemplate cardData, float cardSizeX, float cardSizeY){
        var iconSize = cardSizeX/2f;
        var cardIcon = new Label("");

        var texture = Main.getInstance().getAssetManager().loadTexture(cardData.getIconPath());
        texture.setMagFilter(Texture.MagFilter.Nearest);
        texture.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
        var background = new QuadBackgroundComponent(texture);
        cardIcon.setBackground(background);

        cardIcon.setPreferredSize(
                new Vector3f(
                        iconSize,
                        iconSize,
                        1
                )
        );
        return cardIcon;
    }

    private Label getCardDescription(AugmentCardTemplate cardData){
        var cardDescription = new Label("");
        var font = Main.getInstance().getAssetManager().loadFont("Interface/Fonts/pixelFlat11.fnt");
        cardDescription.setFont(font);
        cardDescription.setFontSize(INITIAL_CARD_DESCRIPTION_FONT_SIZE * getWidthRatio());
        cardDescription.setText(cardData.getDescription());
        cardDescription.setTextHAlignment(HAlignment.Center);
        return cardDescription;
    }

    public void pick(){
        onCardChoose.accept(cardData);
    }

    public Spatial scaleRelativeToInitialSize(float newSize) {
        var sizeDifference = newSize - 1;
        var distanceToMove = sizeDifference/2;
        super.setPreferredSize(initialCardSize.mult(newSize));
        cardNameLabel.setFontSize(INITIAL_CARD_NAME_FONT_SIZE*newSize*getWidthRatio());
        cardDescriptionLabel.setFontSize(INITIAL_CARD_DESCRIPTION_FONT_SIZE*newSize*getWidthRatio());

        setLocalTranslation(
                initialCardPos.subtract(initialCardSize.getX()*distanceToMove, initialCardSize.getY()*-distanceToMove,initialCardSize.getZ())
        );
        return this;
    }
}
