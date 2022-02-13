package cz.tomet123.server.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MapInfoJson {

    int sizeW;
    int sizeH;
    PosJson HLeftCorner;
    PosJson LRightCorner;
}

