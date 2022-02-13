package com.example.server.command.dev;

import com.example.server.Provider.GamePlayer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.ConsoleSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import org.jetbrains.annotations.NotNull;

public class TestScoreCommand extends Command {


    public TestScoreCommand() {
        super("addScore");
        setDefaultExecutor(this::usage);
    }


    private void usage(@NotNull CommandSender commandSender, @NotNull CommandContext commandContext) {

        if(commandSender instanceof ConsoleSender) return;

        GamePlayer p = (GamePlayer) commandSender;

        p.sendMessage("Not consumable: "+p.addScore(3));
    }


}
