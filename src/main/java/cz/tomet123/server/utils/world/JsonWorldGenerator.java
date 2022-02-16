package cz.tomet123.server.utils.world;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.tomet123.server.utils.pojo.BlockWitPosJson;
import net.minestom.server.instance.ChunkGenerator;
import net.minestom.server.instance.ChunkPopulator;
import net.minestom.server.instance.batch.ChunkBatch;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;


public abstract class JsonWorldGenerator implements ChunkGenerator {

    private final String mapClassPath;

    public JsonWorldGenerator(String mapClassPath) {
        this.mapClassPath = mapClassPath;

    }

    @Override
    public void generateChunkData(@NotNull ChunkBatch batch, int chunkX, int chunkZ) {
        try {
            generateFromJson(mapClassPath, batch, chunkX, chunkZ);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateFromJson(String world, ChunkBatch batch, int chunkX, int chunkZ) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        if (JsonWorldGenerator.class.getResource("/" + world + "/" + chunkX + "-" + chunkZ + ".json") != null) {
            List<BlockWitPosJson> blockWitPosList = objectMapper.readValue(
                    JsonWorldGenerator.class.getResourceAsStream("/" + world + "/" + chunkX + "-" + chunkZ + ".json"),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, BlockWitPosJson.class));
            blockWitPosList.forEach(b -> batch.setBlock(b.getX(), b.getY(), b.getZ(), Block.fromNamespaceId(b.getBlockID())));
        }
    }

    @Override
    public List<ChunkPopulator> getPopulators() {
        return null;
    }

}
