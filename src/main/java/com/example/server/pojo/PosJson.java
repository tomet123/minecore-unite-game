package com.example.server.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minestom.server.coordinate.Pos;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PosJson {

    int x;
    int y;
    int z;


    public Pos getMinestomPos() {
        return new Pos(x, y, z);
    }
}
