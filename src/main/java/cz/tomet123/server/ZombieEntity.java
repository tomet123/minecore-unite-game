package cz.tomet123.server;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.ai.EntityAIGroupBuilder;
import net.minestom.server.entity.ai.goal.CombinedAttackGoal;
import net.minestom.server.entity.ai.goal.FollowTargetGoal;
import net.minestom.server.entity.ai.goal.MeleeAttackGoal;
import net.minestom.server.entity.ai.goal.RandomLookAroundGoal;
import net.minestom.server.entity.ai.target.ClosestEntityTarget;
import net.minestom.server.entity.ai.target.LastEntityDamagerTarget;
import net.minestom.server.utils.time.TimeUnit;

import java.time.Duration;
import java.time.temporal.TemporalUnit;


public class ZombieEntity extends EntityCreature {

    private Pos spawnPos;

    public ZombieEntity() {
        super(EntityType.ZOMBIE);
        addAIGroup(
                new EntityAIGroupBuilder()
                        .addGoalSelector(new FollowTargetGoal(this, Duration.ofMillis(10)))
                        .addGoalSelector(new MeleeAttackGoal(this,2, Duration.ofMillis(800)))
                        .addTargetSelector(new ClosestEntityTarget(this,5, Player.class))
                        .build()
        );
    }
}
