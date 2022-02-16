package cz.tomet123.server;

import cz.tomet123.server.utils.spell.BaseSpell;
import net.minestom.server.item.Material;

public class ExampleSpell extends BaseSpell {


    @Override
    public Material getIcon() {
        return Material.SLIME_BALL;
    }
}
