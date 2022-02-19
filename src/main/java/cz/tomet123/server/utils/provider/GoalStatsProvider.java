package cz.tomet123.server.utils.provider;

import cz.tomet123.server.utils.player.GamePlayer;
import net.minestom.server.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class GoalStatsProvider {

    Map<Player,Integer> statistics = new HashMap<>();

    public void clearStats(){
        statistics.clear();
    }

    public void addStats(Player player,int score){
        statistics.putIfAbsent(player,0);
        statistics.compute(player,(gamePlayer, integer) -> integer+score);
    }

    @Override
    public String toString() {
        return "GoalStatsProvider{" +
                "statistics=" + statistics +
                '}';
    }
}
