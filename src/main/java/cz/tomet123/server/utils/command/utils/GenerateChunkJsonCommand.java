package cz.tomet123.server.utils.command.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.tomet123.server.utils.pojo.BlockWitPosJson;
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

public class GenerateChunkJsonCommand extends Command {

    ObjectMapper objectMapper = new ObjectMapper();

    int minY = 0;
    int maxY = 255;

    public GenerateChunkJsonCommand(String cmd) {
        super(cmd);
        setDefaultExecutor(this::usage);
    }


    private void usage(@NotNull CommandSender commandSender, @NotNull CommandContext commandContext) {

        if (commandSender instanceof ConsoleSender) return;

        Player p = (Player) commandSender;

        int chunkX = p.getPosition().chunkX();
        int chunkZ = p.getPosition().chunkZ();


        List<BlockWitPosJson> blockMap = new ArrayList<>();
        for (int z = chunkZ * 16; z < chunkZ * 16 + 16; z++) {
            for (int x = chunkX * 16; x < chunkX * 16 + 16; x++) {
                for (int y = minY; y <= maxY; y++) {
                    Block b = p.getInstance().getBlock(x, y, z);
                    if (!b.isAir()) {
                        blockMap.add(new BlockWitPosJson(x, y, z, b.name(), b.nbt()));
                    }
                }
            }
        }
        File directory = new File("chunk-json");
        if (!directory.exists()) {
            directory.mkdir();
            directory.deleteOnExit();
        }
        try {
            objectMapper.writeValue(new File("chunk-json/" + chunkX + "-" + chunkZ + ".json"), blockMap);
            p.sendMessage("Chunk file generated ok");
            return;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        p.sendMessage("Chunk file generated failed seee console");
    }


}
