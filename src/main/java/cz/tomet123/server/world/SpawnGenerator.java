package cz.tomet123.server.world;


import cz.tomet123.server.utils.JsonWorldPlacer;
import net.minestom.server.instance.ChunkGenerator;
import net.minestom.server.instance.ChunkPopulator;
import net.minestom.server.instance.batch.ChunkBatch;

import java.io.IOException;
import java.util.List;

public class SpawnGenerator implements ChunkGenerator {
/*
    private Block getBlock(int x, int y){
        if(x%2==0){
            if(y%2==0) {
                return Block.IRON_BLOCK;
            }else {
                return Block.GOLD_BLOCK;
            }
        }else {
            if(y%2==0) {
                return Block.GOLD_BLOCK;
            }else {
                return Block.IRON_BLOCK;
            }
        }

    }

    @Override
    public void generateChunkData(ChunkBatch batch, int chunkX, int chunkZ) {
        // Set chunk blocks
        for (byte x = 0; x < Chunk.CHUNK_SIZE_X; x++)
            for (byte z = 0; z < Chunk.CHUNK_SIZE_Z; z++) {
                    if(x==15 || z == 15 || x==0 || z==0) batch.setBlock(x, 40, z, getBlock(chunkX,chunkZ));
                    else batch.setBlock(x, 40, z, Block.STONE);
            }
    }
*/

    @Override
    public void generateChunkData(ChunkBatch batch, int chunkX, int chunkZ) {
        try {
            JsonWorldPlacer.generateFromJson("world/lobby", batch, chunkX, chunkZ);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public List<ChunkPopulator> getPopulators() {
        return null;
    }
}
