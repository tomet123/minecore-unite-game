package com.example.server.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlockWitPosJson {

    int x;
    int y;
    int z;

    String blockID;
    NBTCompound nbtCompound;


}
