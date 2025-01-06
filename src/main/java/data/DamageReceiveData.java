package data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DamageReceiveData {
    private int victimId;
    private int attackerId;
    private float rawDamage;
    public DamageReceiveData(int victimId,int attackerId,float rawDamage){
        this.victimId = victimId;
        this.attackerId = attackerId;
        this.rawDamage = rawDamage;
    }
}
