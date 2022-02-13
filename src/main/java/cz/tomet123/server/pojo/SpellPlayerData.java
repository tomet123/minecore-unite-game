package cz.tomet123.server.pojo;

import cz.tomet123.server.Spell.BaseSpell;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SpellPlayerData {

    boolean active;
    int lastSendDisable;

    int cooldown;
    int cooldownCounter;


    BaseSpell spell;
}
