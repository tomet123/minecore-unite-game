package cz.tomet123.server.utils;

import cz.tomet123.server.pojo.BlockWitPosJson;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minestom.server.instance.batch.ChunkBatch;
import net.minestom.server.instance.block.Block;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class JsonWorldPlacer {


    public static void generateFromJson(String world, ChunkBatch batch, int chunkX, int chunkZ) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        if (JsonWorldPlacer.class.getResource("/" + world + "/" + chunkX + "-" + chunkZ + ".json") != null) {
            List<BlockWitPosJson> blockWitPosList = objectMapper.readValue(
                    JsonWorldPlacer.class.getResourceAsStream("/" + world + "/" + chunkX + "-" + chunkZ + ".json"),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, BlockWitPosJson.class));
            blockWitPosList.stream().filter(blockWitPosJson -> !(blockWitPosJson.getY() == 40 && Block.fromNamespaceId(blockWitPosJson.getBlockID()).equals(Block.STONE))).toList().forEach(b -> batch.setBlock(b.getX(), b.getY(), b.getZ(), Block.fromNamespaceId(b.getBlockID())));
        }
    }
}
