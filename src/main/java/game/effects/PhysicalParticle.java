package game.effects;

import game.effects.particleStrategies.ParticleMovementStrategy;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class PhysicalParticle {

    @Getter
    private final ParticleMovementStrategy strategy;
    
}
