package game.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class FloatAttribute extends Attribute{
    @Getter
    @Setter
    private float value;
}
