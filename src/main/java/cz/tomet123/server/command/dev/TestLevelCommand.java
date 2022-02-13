package cz.tomet123.server.command.dev;

import cz.tomet123.server.Provider.GamePlayer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.ConsoleSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import org.jetbrains.annotations.NotNull;

public class TestLevelCommand extends Command {


    public TestLevelCommand() {
        super("addLevel");
        setDefaultExecutor(this::usage);
    }


    private void usage(@NotNull CommandSender commandSender, @NotNull CommandContext commandContext) {

        if (commandSender instanceof ConsoleSender) return;

        GamePlayer p = (GamePlayer) commandSender;

        p.addLevel(0.3f);
    }


}
