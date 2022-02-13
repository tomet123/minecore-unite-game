package cz.tomet123.server.Provider;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.player.PlayerConnection;
import net.minestom.server.scoreboard.Team;
import net.minestom.server.scoreboard.TeamBuilder;
import net.minestom.server.timer.Task;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class GamePlayer extends Player {


    private Task nicKUpdateTask = null;
    private Team team = null;
    //nick
    private int levelLastSend = -1;
    private int scoreLastSend = -1;
    private TeamType teamTypeLastSend = TeamType.NOSET;
    //xpBar
    private int xpLevelLastSend = -1;
    private float xpNextLevelLastSend = -1;
    //inventory
    private int invScoreLastSend = -1;
    //RealData
    private int level = 0;
    private float nextLevel = 0;
    private int score = 0;
    private TeamType teamType = TeamType.INLOBBY;
    //max
    private int maxScore = 30;


    public GamePlayer(@NotNull UUID uuid, @NotNull String username, @NotNull PlayerConnection playerConnection) {
        super(uuid, username, playerConnection);

        buildDefaultTeam();
    }

    @Override
    public void update(long time) {
        super.update(time);

        UpdateTeamName();
        UpdateXpBasedOnLevel();
        UpdateInventory();
    }

    public void addLevel(float x) {
        nextLevel += x;
        while (nextLevel > 1) {
            level += 1;
            nextLevel -= 1;
        }
    }

    public int addScore(int x) {

        if (score + x > maxScore) {
            int t = score + x;
            score = maxScore;
            return t - maxScore;
        } else {
            score += x;
        }
        return 0;
    }

    @Override
    public void remove() {
        super.remove();
        if (nicKUpdateTask != null) nicKUpdateTask.cancel();
        nicKUpdateTask = null;
    }

    private void buildDefaultTeam() {
        TeamBuilder t = new TeamBuilder("nick_" + Objects.requireNonNull(playerConnection.getPlayer()).getUsername(), MinecraftServer.getTeamManager());
        team = t.build();
        setTeam(team);

    }


    private void UpdateInventory() {
        getInventory().setItemStack(8, ItemStack.of(Material.EXPERIENCE_BOTTLE, score));
    }

    private void UpdateXpBasedOnLevel() {
        if (xpLevelLastSend != level) {
            setLevel(level);
            xpLevelLastSend = level;
        }
        if (xpNextLevelLastSend != nextLevel) {
            setExp(nextLevel);
            xpNextLevelLastSend = nextLevel;
        }

    }

    private void UpdateTeamName() {
        if (level != levelLastSend || score != scoreLastSend || teamType != teamTypeLastSend) {
            team.setPrefix(Component.text(level + " ", NamedTextColor.AQUA));
            team.setSuffix(Component.text(" (" + score + ")", NamedTextColor.LIGHT_PURPLE));

            switch (teamType) {
                case LEFT -> team.setTeamColor(NamedTextColor.GOLD);
                case RIGHT -> team.setTeamColor(NamedTextColor.BLUE);
                case INLOBBY -> team.setTeamColor(NamedTextColor.BLACK);
                case INSTART -> team.setTeamColor(NamedTextColor.GREEN);
            }
            levelLastSend = level;
            scoreLastSend = score;
            teamTypeLastSend = teamType;

            team.sendUpdatePacket();
        }
    }

    enum TeamType {
        LEFT, RIGHT, INSTART, INLOBBY, NOSET
    }


}
