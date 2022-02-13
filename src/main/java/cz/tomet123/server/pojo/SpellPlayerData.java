package cz.tomet123.server.pojo;

import cz.tomet123.server.Spell.BaseSpell;
import cz.tomet123.server.Spell.ExampleSpell;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SpellPlayerData {


    public SpellPlayerData(state state,int cooldown, BaseSpell spell){
       this.state=state;
       this.lastSendDisable=0;
       this.cooldown=cooldown;
       this.cooldownCounter=0;
       this.spell=spell;
    }

    public enum state{
        DISABLED,READY,COOLDOWN
    }

    state state;
    int lastSendDisable;

    int cooldown;
    int cooldownCounter;


    BaseSpell spell;
}
