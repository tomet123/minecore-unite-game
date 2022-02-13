package com.example.server.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.minestom.server.coordinate.Pos;

@Data
@AllArgsConstructor
public class MapPos {

    Pos p;
    int mapId;

    int x;
    int y;
    int z;

    public int getLeft(){
        return z;
    }

    public int getTop(){
        return y;
    }
}
