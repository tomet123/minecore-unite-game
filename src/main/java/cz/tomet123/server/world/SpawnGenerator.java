package cz.tomet123.server.world;


import cz.tomet123.server.utils.JsonWorldPlacer;
import net.minestom.server.instance.ChunkGenerator;
import net.minestom.server.instance.ChunkPopulator;
import net.minestom.server.instance.batch.ChunkBatch;

import java.io.IOException;
import java.util.List;

public class SpawnGenerator implements ChunkGenerator {

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
