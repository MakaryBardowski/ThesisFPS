package game.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class IntegerAttribute extends Attribute{
    @Getter
    @Setter
    private int value;
}
