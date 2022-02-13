package cz.tomet123.server.world;

import cz.tomet123.server.utils.JsonWorldPlacer;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.ChunkGenerator;
import net.minestom.server.instance.ChunkPopulator;
import net.minestom.server.instance.batch.ChunkBatch;
import net.minestom.server.instance.block.Block;

import java.io.IOException;
import java.util.List;

public class StoneGenerator implements ChunkGenerator {

    @Override
    public void generateChunkData(ChunkBatch batch, int chunkX, int chunkZ) {
        try {
            JsonWorldPlacer.generateFromJson("world/game", batch, chunkX, chunkZ);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (JsonWorldPlacer.class.getResource("/" + "world/game" + "/" + chunkX + "-" + chunkZ + ".json") == null) {

            for (byte x = 0; x < Chunk.CHUNK_SIZE_X; x++)
                for (byte z = 0; z < Chunk.CHUNK_SIZE_Z; z++) {
                    batch.setBlock(x, 40, z, Block.GRASS_BLOCK);
                    batch.setBlock(x, 39, z, Block.STONE);
                }
        }
    }


    @Override
    public List<ChunkPopulator> getPopulators() {
        return null;
    }
}
