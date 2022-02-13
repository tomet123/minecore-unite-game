package com.example.server.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.minestom.server.coordinate.Pos;

@Data
@AllArgsConstructor
public class MapPos {

    public enum rotation{
        X,Z
    }

    Pos p;
    int mapId;

    int x;
    int y;
    int z;

    public int getLeft(rotation r) {
        if(rotation.X.equals(r))return x;
        if(rotation.Z.equals(r))return z;
        return -1;
    }
    //TODO make this dynamic
    public int getTop() { return y; }
}
