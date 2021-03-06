package cz.tomet123.server.command.dev;

import cz.tomet123.server.utils.player.GamePlayer;
import lombok.SneakyThrows;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.ConsoleSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import org.jetbrains.annotations.NotNull;

public class TestCooldownCommand extends Command {


    public TestCooldownCommand() {
        super("testCooldown");
        setDefaultExecutor(this::usage);
    }


    @SneakyThrows
    private void usage(@NotNull CommandSender commandSender, @NotNull CommandContext commandContext) {

        if (commandSender instanceof ConsoleSender) return;

        GamePlayer p = (GamePlayer) commandSender;

        p.enableEffect(1);

        Thread.sleep(1000);

        p.useEffect(1);

        while (!p.canUseEffect(1)){
            Thread.sleep(100);
            System.out.println("Check "+p.canUseEffect(1));
        };

        p.sendMessage("can use agin"+p.canUseEffect(1));
    }


}
