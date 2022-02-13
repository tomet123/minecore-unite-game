package cz.tomet123.server.map;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.metadata.other.ItemFrameMeta;
import net.minestom.server.instance.Instance;

import java.awt.*;

public class LobbyMapMonitor extends BaseMapMonitor {


    protected LobbyMapMonitor(Instance i, String name) {
        super(i, name);
        setHLeftCorner(new Pos(-1, 48, 25));
        setLRightCorner(new Pos(-1, 42, 6));
        setSizeH(7);
        setSizeW(20);
        setOrientation(ItemFrameMeta.Orientation.EAST);
        setYaw(-90);
        initInternal();

    }

    @Override
    protected void renderer(Graphics2D renderer) {
        renderer.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        renderer.setColor(Color.BLACK);
        renderer.clearRect(0, 0, 128 * getSizeW(), 128 * getSizeH());
        renderer.setColor(Color.RED);
        renderer.setFont(new Font("TimesRoman", Font.BOLD, 100));
        renderer.drawString("Minecore.cz", 0, 110);
        renderer.setColor(Color.GREEN);
        renderer.drawString("Minecraft Unite Games", 1000, 150);
        renderer.setFont(new Font("TimesRoman", Font.BOLD, 30));
        renderer.setColor(Color.YELLOW);
        renderer.drawString("Minihra: test123", 1500, 300);
        renderer.setFont(new Font("TimesRoman", Font.BOLD, 30));
        renderer.setColor(Color.YELLOW);
        renderer.drawString("Pravidla:", 2000, 300);
    }


}
