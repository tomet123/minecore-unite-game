package cz.tomet123.server.utils.world;

import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.ChunkGenerator;
import net.minestom.server.instance.ChunkPopulator;
import net.minestom.server.instance.batch.ChunkBatch;
import net.minestom.server.instance.block.Block;

import java.util.List;

public class FlatWorldGenerator implements ChunkGenerator {

    Block out;
    Block in;
    int y;

    public FlatWorldGenerator(Block out, Block in, int y){
        this.out=out;
        this.in=in;
        this.y=y;
    }

    @Override
    public void generateChunkData(ChunkBatch batch, int chunkX, int chunkZ) {
        for (byte x = 0; x < Chunk.CHUNK_SIZE_X; x++)
                for (byte z = 0; z < Chunk.CHUNK_SIZE_Z; z++) {
                    batch.setBlock(x, y, z, out);
                    batch.setBlock(x, y-1, z, in);
                }
    }


    @Override
    public List<ChunkPopulator> getPopulators() {
        return null;
    }
}
