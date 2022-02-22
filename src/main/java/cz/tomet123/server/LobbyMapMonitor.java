package cz.tomet123.server;

import cz.tomet123.server.utils.map.BaseMapMonitor;
import cz.tomet123.server.utils.provider.GoalStatsProvider;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.other.ItemFrameMeta;
import net.minestom.server.instance.Instance;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Set;

public class LobbyMapMonitor extends BaseMapMonitor {

    private GoalStatsProvider goalStatsProvider;

    public LobbyMapMonitor(GoalStatsProvider goalStatsProvider) {
        setHLeftCorner(new Pos(-1, 48, 25));
        setLRightCorner(new Pos(-1, 42, 6));
        setSizeH(7);
        setSizeW(20);
        setOrientation(ItemFrameMeta.Orientation.EAST);

        this.goalStatsProvider=goalStatsProvider;

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
        renderer.drawString("Pravidla:"+ System.currentTimeMillis(), 2000, 300);

        renderer.drawString("Stats: ", 1000, 300);
        renderer.setFont(new Font("TimesRoman", Font.BOLD, 10));
        renderer.setColor(Color.YELLOW);
        renderer.setColor(Color.GREEN);
        int x = 400;
        for (Map.Entry<Player,Integer> d : goalStatsProvider.getStatistics().entrySet()) {
            renderer.drawString(d.getKey().getUsername()+"    "+d.getValue(), 1000, x);
            renderer.setFont(new Font("TimesRoman", Font.BOLD, 15));
            renderer.setColor(Color.GREEN);
            x+=10;
        }

    }


}
