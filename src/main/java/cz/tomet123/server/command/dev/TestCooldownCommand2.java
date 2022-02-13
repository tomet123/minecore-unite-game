package cz.tomet123.server.command.dev;

import cz.tomet123.server.Provider.GamePlayer;
import lombok.SneakyThrows;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.ConsoleSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import org.jetbrains.annotations.NotNull;

public class TestCooldownCommand2 extends Command {


    public TestCooldownCommand2() {
        super("checkCooldown");
        setDefaultExecutor(this::usage);
    }


    @SneakyThrows
    private void usage(@NotNull CommandSender commandSender, @NotNull CommandContext commandContext) {

        if (commandSender instanceof ConsoleSender) return;

        GamePlayer p = (GamePlayer) commandSender;

        p.sendMessage("can use agin"+p.canUseEffect(1));
    }


}
