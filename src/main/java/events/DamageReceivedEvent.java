package events;

import com.jme3.math.Vector3f;
import data.DamageReceiveData;
import lombok.Getter;
import lombok.Setter;

public class DamageReceivedEvent extends GameEvent {
    @Getter
    @Setter
    private DamageReceiveData damageData;
    public DamageReceivedEvent(DamageReceiveData damageData){
        this.damageData = damageData;
    }
}
