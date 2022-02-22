package cz.tomet123.server.utils.goalZone;

import cz.tomet123.server.utils.provider.GoalStatsProvider;
import cz.tomet123.server.utils.player.GamePlayer;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.other.ArmorStandMeta;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerStartSneakingEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.timer.ExecutionType;
import net.minestom.server.timer.SchedulerManager;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


@Slf4j
public class GoalZone {
    private final int radius=2;
    private final int goalTime=400;
    private final Point center;
    private final int initialScore;
    private final boolean finite;

    private final GoalZone parentGoalZone;
    private final GamePlayer.TeamType team;

    private Function update;

    private Instance instance;
    private Entity holo;
    @Setter
    private GoalStatsProvider goalStatsProvider;

    private int score;

    List<Pos> validZone = new ArrayList<>();
    List<Pos> border = new ArrayList<>();


    public GoalZone(Point center, int points, boolean finite, GoalZone parentGoalZone, GamePlayer.TeamType team) {

        this.center = center;
        this.initialScore = points;
        this.finite = finite;
        this.parentGoalZone = parentGoalZone;
        this.team = team;

        generateCircle();
        System.out.println(parentGoalZone);
        if(parentGoalZone!=null) {
            parentGoalZone.goalZoneUpdateFallback((cg -> {
                this.updateHolo();
                this.placeGoalZone(cg.isEmpty());
                return null;
            }));
        }

    }

    public GamePlayer.TeamType getTeamAllowedToGoal(){
        if(team== GamePlayer.TeamType.ORANGE){
            return GamePlayer.TeamType.BLUE;
        }else if(team== GamePlayer.TeamType.BLUE){
            return GamePlayer.TeamType.ORANGE;
        }else {
            return null;
        }
    }

    public void goalZoneUpdateFallback(Function<GoalZone,Void> f){
        update=f;
    }

    private boolean isAllowedToGoal(GamePlayer p){
        if(!isEmpty() && getTeamAllowedToGoal()==p.getTeamType() ){
            if(parentGoalZone!=null && parentGoalZone.isEmpty()) {
                return true;
            }else if(parentGoalZone!=null && !parentGoalZone.isEmpty()){
                return false;
            }else {
                return true;
            }

        }
        return false;
    }

    private void placeHolo(){

        holo = new Entity(EntityType.ARMOR_STAND);
        updateHolo();
        Pos x = new Pos(center.blockX(),center.blockY()+1,center.blockZ());
        holo.setInstance(instance, x);
        holo.spawn();
    }

    private void updateHolo(){
        ArmorStandMeta meta = (ArmorStandMeta) holo.getEntityMeta();
        if(parentGoalZone!=null) {
            meta.setCustomNameVisible(parentGoalZone.isEmpty());
        }else {
            meta.setCustomNameVisible(true);
        }
        meta.setCustomName(Component.text("Score "+score+"/"+initialScore));
        meta.setHasNoGravity(true);
        meta.setInvisible(true);
    }

    private void generateCircle(){

        for (int j = radius*-1; j <=radius ; j++) {
            int l=radius;
            if(j==radius||j==radius*-1) l=radius-1;

            for (int i = l*-1; i <=l ; i++) validZone.add(new Pos(center.blockX()+i,center.blockY(),center.blockZ()+j));
            for (int i = l*-1; i <=l ; i++) validZone.add(new Pos(center.blockX()+j,center.blockY(),center.blockZ()+i));
        }
        int l =radius-1;
        for (int i = l*-1; i <=l ; i++) border.add(new Pos(center.blockX()+(radius+1),center.blockY(),center.blockZ()+i));
        for (int i = l*-1; i <=l ; i++) border.add(new Pos(center.blockX()-(radius+1),center.blockY(),center.blockZ()+i));
        for (int i = l*-1; i <=l ; i++) border.add(new Pos(center.blockX()+i,center.blockY(),center.blockZ()+(radius+1)));
        for (int i = l*-1; i <=l ; i++) border.add(new Pos(center.blockX()+i,center.blockY(),center.blockZ()-(radius+1)));

        border.add(new Pos(center.blockX()+radius,center.blockY(),center.blockZ()+radius));
        border.add(new Pos(center.blockX()+radius,center.blockY(),center.blockZ()-radius));
        border.add(new Pos(center.blockX()-radius,center.blockY(),center.blockZ()+radius));
        border.add(new Pos(center.blockX()-radius,center.blockY(),center.blockZ()-radius));
    }

    private void setBLock(Instance i,Pos pos,Block b){
        instance=i;
        i.setBlock(pos, b);
    }

    private boolean isInGoal(Player p){
        return p.getInstance().equals(instance) && validZone.stream().filter(pos -> p.getPosition().sameBlock(pos)).count()>0;
    }

    private void placeGoalZone(boolean allowed) {
        Block centerActive;
        Block centerInactive;
        Block borderActive;
        Block borderInactive;
        if(team== GamePlayer.TeamType.BLUE){
            centerActive=Block.LIGHT_BLUE_CARPET;
            centerInactive=Block.BLUE_CARPET;
            borderActive=Block.CUT_COPPER_SLAB;
            borderInactive=Block.STONE_SLAB;
        }else if(team== GamePlayer.TeamType.ORANGE){
            centerActive=Block.ORANGE_CARPET;
            centerInactive=Block.RED_CARPET;
            borderActive=Block.CUT_COPPER_SLAB;
            borderInactive=Block.STONE_SLAB;
        }else {
            throw new RuntimeException("invalid team");
        }

        if(allowed==true) {
            validZone.forEach(point -> setBLock(instance, point, centerActive));
            border.forEach(point -> setBLock(instance, point, borderActive));
        }else {
            validZone.forEach(point -> setBLock(instance, point, centerInactive));
            border.forEach(point -> setBLock(instance, point, borderInactive));
        }
    }

    private void removeGoalZone(){
        validZone.forEach(point -> setBLock(instance,point,Block.AIR));
        border.forEach(point -> setBLock(instance,point,Block.AIR));
    }
    private void removeHolo(){
        holo.remove();
    }

    private void checkEmpty(){
        if(isEmpty()){
               log.info("goal zone Full - Goal zone has score: "+score+"/"+initialScore);
               removeGoalZone();
               removeHolo();
        }

    }

    private boolean isEmpty(){
        if(finite){
            if(score> initialScore){
                return true;
            }
        }
        return false;
    }

    private void createGoalEvent(){
        EventNode<PlayerEvent> playerNode = EventNode.type("player-listener-goal", EventFilter.PLAYER,(playerEvent, player) -> isInGoal(player));

        playerNode.addListener(PlayerStartSneakingEvent.class, playerStartSneakingEvent -> {
            GamePlayer p = (GamePlayer) playerStartSneakingEvent.getPlayer();
            SchedulerManager scheduler = MinecraftServer.getSchedulerManager();
            scheduler.buildTask(() -> goalZoneAsyncProcessor(p,this,update)).executionType(ExecutionType.ASYNC).schedule();
        });


        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addChild(playerNode);
    }

    private static void goalZoneAsyncProcessor(GamePlayer gamePlayer,GoalZone goalZone, Function update){
        try{
            if(!goalZone.isAllowedToGoal(gamePlayer)){
                gamePlayer.sendMessage("Tady se nemuzes vysypat");
                return;
            }
            if(gamePlayer.getScore()==0){
                log.debug("Player "+gamePlayer.getUsername()+" Try to goal with empty score");
                return;
            }
            int currentGoalTime= (int)(goalZone.goalTime*gamePlayer.getPercentageFullScore());
            log.debug("Player "+gamePlayer.getUsername()+" goal speed ("+gamePlayer.getPercentageFullScore()+") set to "+currentGoalTime);
            gamePlayer.setScoring(currentGoalTime/4);
            for (int i = 0; i < currentGoalTime; i++) {
                if(!gamePlayer.isSneaking()){
                    goalZone.checkEmpty();
                    goalZone.updateHolo();
                    gamePlayer.cleanScoring();
                    log.info("Player "+gamePlayer.getUsername()+" goal failed: "+gamePlayer.getScore()+" - Goal zone has score: "+goalZone.score+"/"+goalZone.initialScore);
                    return;
                }
                Thread.sleep(10);
            }
            gamePlayer.sendMessage("goal");
            gamePlayer.cleanScoring();
            goalZone.score+=gamePlayer.getScore();
            log.info("Player "+gamePlayer.getUsername()+" goal: "+gamePlayer.getScore()+" - Goal zone has score: "+goalZone.score+"/"+goalZone.initialScore);
            if(goalZone.goalStatsProvider!=null)goalZone.goalStatsProvider.addStats(gamePlayer,gamePlayer.getScore());
            gamePlayer.cleanScore();
            goalZone.checkEmpty();
            goalZone.updateHolo();
            if(update!=null) {
                  update.apply(goalZone);
            }
        }catch (Exception e){
            log.error(gamePlayer.getUsername()+" goal failed",e);
        }

    }

    public void initialize(Instance instance){
        this.instance = instance;
        if(parentGoalZone!=null) {
            placeGoalZone(parentGoalZone.isEmpty());
        }else {
            placeGoalZone(true);
        }
        createGoalEvent();
        placeHolo();
    }
}
