package game.effects.particleStrategies;

import com.jme3.scene.Node;
import game.effects.PhysicalParticleControl;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public abstract class ParticleMovementStrategy {

    @Getter
    protected final Node node;

    public abstract void updateParticle(float tpf);
    
    public abstract void move(float tpf);

    public abstract void onHitGround();

    protected abstract boolean hasNotHitGround();

    protected void removeControl() {
        node.removeControl(PhysicalParticleControl.class);
    }

}
