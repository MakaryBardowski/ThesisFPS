package guiComponents;

import client.appStates.ClientGameAppState;
import client.Main;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.core.GuiControl;
import game.entities.mobs.player.Player;
import lombok.Getter;

public class LemurPlayerHealthbar {

    @Getter
    private final Vector2f normalizedHealthPercentAndChange = new Vector2f();
    private final Player player;

    float timeSinceLastHealthbarChange = 0;
    float waitTimeForHealthbarColorUpdate = 0.65f;

    private Container healthbarContainer;

    static {
        GuiGlobals.getInstance().setCursorEventsEnabled(false);
    }

    public LemurPlayerHealthbar(Player player) {
        this.player = player;
        healthbarContainer = addHealthbar(Main.getInstance().getGuiNode());
    }

    private Container addHealthbar(Node guiNode) {
        var healthbarContainer = new Container();
        guiNode.attachChild(healthbarContainer);
        GuiControl gc = new GuiControl("");

        var healthbarNode = createHealthbarNode();
        healthbarNode.addControl(gc);
//        myWindow.setLocalTranslation(30, 440, 0);
        healthbarContainer.setLocalTranslation(0, 0, 0);

        healthbarContainer.addChild(healthbarNode);
        return healthbarContainer;
    }

    public void setHealthbarParams(float healthPercent, float healthPercentChange) {
        var totalChange = normalizedHealthPercentAndChange.getY() + healthPercentChange;
        if (healthPercentChange > 0) { // if damage is received, restart timer
            timeSinceLastHealthbarChange = 0;
        } else if (totalChange < 0) { // if in total we receive a healing
            timeSinceLastHealthbarChange = waitTimeForHealthbarColorUpdate;
        }
        normalizedHealthPercentAndChange.setX(healthPercent);
        normalizedHealthPercentAndChange.setY(totalChange);
    }

    private Node createHealthbarNode() {
        var clientGameAppState = ClientGameAppState.getInstance();
        var percentHealth = player.getHealth() / player.getMaxHealth();
        setHealthbarParams(percentHealth, 0);

        var assetManager = Main.getInstance().getAssetManager();

        var percentWidthTaken = 0.20f;
        var percentHeightTaken = 0.03f;

        var screenWidtth = clientGameAppState.getSettings().getWidth() * percentWidthTaken;
        var screenHeight = clientGameAppState.getSettings().getHeight() * percentHeightTaken;
        Quad quad = new Quad(screenWidtth, screenHeight);
        Geometry geo = new Geometry("OurQuad", quad);
        Material mat = new Material(assetManager, "Shaders/PlayerHealthbar/SimpleColor.j3md");
        var emptyHpBar = assetManager.loadTexture("Textures/GUI/HUD/playerHealthbar/healthbar_background.png");
        var fullWithoutFrame = assetManager.loadTexture("Textures/GUI/HUD/playerHealthbar/healthbar.png");

        emptyHpBar.setMagFilter(Texture.MagFilter.Nearest);
        fullWithoutFrame.setMagFilter(Texture.MagFilter.Nearest);

        mat.setTexture("emptyHealthBar", emptyHpBar);
        mat.setTexture("fullHealthBarWithoutFrame", fullWithoutFrame);
        mat.setVector2("healthPercent", normalizedHealthPercentAndChange);

        mat.setColor("tint", new ColorRGBA(154f / 255f, 0f / 255f, 0f / 255f, 1f / 255f));
        geo.setMaterial(mat);
        Node healthbarNode = new Node();
        healthbarNode.attachChild(geo);
        return healthbarNode;
    }

    public void updateHealthbar(float tpf) {
        timeSinceLastHealthbarChange += tpf;

        if (timeSinceLastHealthbarChange < waitTimeForHealthbarColorUpdate) {
            return;
        }
        float changeRateDamage = 0.4f;
        float changeRateHeal = 0.2f;

        if (normalizedHealthPercentAndChange.getY() > 0) {
            normalizedHealthPercentAndChange.setY(normalizedHealthPercentAndChange.getY() - tpf * changeRateDamage > 0 ? normalizedHealthPercentAndChange.getY() - tpf * changeRateDamage : 0);
        } else if (normalizedHealthPercentAndChange.getY() < 0) {
            normalizedHealthPercentAndChange.setY(normalizedHealthPercentAndChange.getY() + tpf * changeRateHeal < 0 ? normalizedHealthPercentAndChange.getY() + tpf * changeRateHeal : 0);
        }
        if (normalizedHealthPercentAndChange.getY() == 0) {
            timeSinceLastHealthbarChange = 0;
        }
    }

    public void cleanup(){
        healthbarContainer.removeFromParent();
    }
}
