package com.example.server.command.dev;

import com.example.server.Provider.GamePlayer;
import com.example.server.pojo.BlockWitPosJson;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.ConsoleSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestLevelCommand extends Command {


    public TestLevelCommand() {
        super("addLevel");
        setDefaultExecutor(this::usage);
    }


    private void usage(@NotNull CommandSender commandSender, @NotNull CommandContext commandContext) {

        if(commandSender instanceof ConsoleSender) return;

        GamePlayer p = (GamePlayer) commandSender;

        p.addLevel(0.3f);
    }


}
