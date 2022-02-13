package cz.tomet123.server.Provider;

import cz.tomet123.server.Spell.ExampleSpell;
import cz.tomet123.server.pojo.SpellPlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.server.play.SetCooldownPacket;
import net.minestom.server.network.player.PlayerConnection;
import net.minestom.server.scoreboard.Team;
import net.minestom.server.scoreboard.TeamBuilder;
import net.minestom.server.timer.ExecutionType;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.Task;
import net.minestom.server.utils.time.TimeUnit;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Semaphore;

public class GamePlayer extends Player {

    private static final int UPDATE_DISABLED_SPELLS=16;
    private static final int DISABLED_SPELLS_TIME=1000;


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

    //spells
    private Map<Integer, SpellPlayerData> spells =new HashMap<>();



    public GamePlayer(@NotNull UUID uuid, @NotNull String username, @NotNull PlayerConnection playerConnection) {
        super(uuid, username, playerConnection);

        buildDefaultTeam();


        //TODO temp added spell - add to kits
        spells.put(1,new SpellPlayerData(SpellPlayerData.state.DISABLED,10,new ExampleSpell()));


        SchedulerManager scheduler = MinecraftServer.getSchedulerManager();
        scheduler.buildTask(() -> updateEffectCooldown()).executionType(ExecutionType.ASYNC).repeat(1, TimeUnit.CLIENT_TICK).schedule();
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

    private void updateEffectCooldown(){

            spells.forEach((integer, spellPlayerData) -> {
                switch (spellPlayerData.getState()){
                    case DISABLED -> {
                        if (spellPlayerData.getLastSendDisable() < UPDATE_DISABLED_SPELLS) {
                            spellPlayerData.setLastSendDisable(spellPlayerData.getLastSendDisable() + 1);
                        } else {
                            spellPlayerData.setLastSendDisable(0);
                            getPlayerConnection().sendPacket(new SetCooldownPacket(getInventory().getItemStack(integer).getMaterial().id(), DISABLED_SPELLS_TIME));
                        }
                    }
                    case COOLDOWN -> {
                        if (spellPlayerData.getCooldownCounter()>0) {
                            spellPlayerData.setCooldownCounter(spellPlayerData.getCooldownCounter()-1);
                        }else spellPlayerData.setState(SpellPlayerData.state.READY);
                    }
                    case READY -> {
                        if(spellPlayerData.getCooldownCounter()!=0)
                        spellPlayerData.setCooldownCounter(0);
                        if(spellPlayerData.getLastSendDisable()!=0)
                            spellPlayerData.setLastSendDisable(0);
                    }
                }
            });

    }

    public void enableEffect(int id){
        if(spells.get(id)==null) return;
            if (spells.get(id).getState().equals(SpellPlayerData.state.DISABLED)) {
                spells.get(id).setState(SpellPlayerData.state.READY);
                spells.get(id).setLastSendDisable(0);
                getPlayerConnection().sendPacket(new SetCooldownPacket(getInventory().getItemStack(id).getMaterial().id(), 0));
            }
    }

    public void disableEffect(int id){
        if(spells.get(id)==null) return;

            if (spells.get(id).getState().equals(SpellPlayerData.state.READY)) {
                spells.get(id).setState(SpellPlayerData.state.DISABLED);
                spells.get(id).setLastSendDisable(0);
                getPlayerConnection().sendPacket(new SetCooldownPacket(getInventory().getItemStack(id).getMaterial().id(), DISABLED_SPELLS_TIME));
            }


    }


    public void useEffect(int id){
        if(spells.get(id)==null) return;
        if(spells.get(id).getState().equals(SpellPlayerData.state.READY)){
            spells.get(id).setState(SpellPlayerData.state.COOLDOWN);
            spells.get(id).setCooldownCounter(spells.get(id).getCooldown());
            getPlayerConnection().sendPacket(new SetCooldownPacket(getInventory().getItemStack(id).getMaterial().id(), spells.get(id).getCooldownCounter()));
        }



    }

    public boolean canUseEffect(int id){
        if(spells.get(id)==null) return false;

            if(spells.get(id).getState().equals(SpellPlayerData.state.READY)){
                return true;
            }

        return false;

    }

    private void buildDefaultTeam() {
        TeamBuilder t = new TeamBuilder("nick_" + Objects.requireNonNull(playerConnection.getPlayer()).getUsername(), MinecraftServer.getTeamManager());
        team = t.build();
        setTeam(team);

    }


    private void UpdateInventory() {
        getInventory().setItemStack(8, ItemStack.of(Material.EXPERIENCE_BOTTLE, score));
        spells.forEach((integer, spellPlayerData) -> {

            getInventory().setItemStack(integer, ItemStack.of(spellPlayerData.getSpell().getIcon(), 1));

        });
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
