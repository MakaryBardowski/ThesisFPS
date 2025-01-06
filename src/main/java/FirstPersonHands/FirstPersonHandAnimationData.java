package FirstPersonHands;

import com.jme3.math.Vector3f;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum FirstPersonHandAnimationData {
    HOLD_PISTOL("HoldPistolR", new Vector3f(0, 0, 0)),
    EQUIP_PISTOL_DOUBLE_GRIP("EquipPistolR", new Vector3f(0, 0, 0)),

    HOLD_LMG("HoldLMG", new Vector3f(0, -0.3f, 1)),
    HOLD_RIFLE("HoldRifle", new Vector3f(0, 0.0f, 1)),
    HOLD_KNIFE("HoldKnife", new Vector3f(0, 0.0f, 0.5f)),
    HOLD_AXE("HoldAxe", new Vector3f(0, -0.15f, 0.6f)),
    HOLD_GRENADE("HoldGrenade", new Vector3f(0, 0.0f, 0.7f));

    @Getter
    private final String animationName;

    @Getter
    private final Vector3f rootOffset;

}
