package cz.tomet123.server.command.dev;

import cz.tomet123.server.ZombieEntity;
import cz.tomet123.server.utils.player.GamePlayer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.ConsoleSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.metadata.LivingEntityMeta;
import org.jetbrains.annotations.NotNull;


public class TestNpcCommand extends Command {


    public TestNpcCommand() {
        super("testNpc");
        setDefaultExecutor(this::usage);
    }


    private void usage(@NotNull CommandSender commandSender, @NotNull CommandContext commandContext) {

        if (commandSender instanceof ConsoleSender) return;

        GamePlayer p = (GamePlayer) commandSender;

        LivingEntity zombie = new ZombieEntity();
        LivingEntityMeta l = zombie.getLivingEntityMeta();
        l.setHealth(1000);
        zombie.setInstance(p.getInstance(),p.getPosition());
        zombie.spawn();

    }


}