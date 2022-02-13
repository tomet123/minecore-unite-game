package cz.tomet123.server.utils;

import net.minestom.server.coordinate.Pos;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class CoordinatesUtils {


    public static <T> List<T> iterateBetwenTwoLocations(Class<T> clazz, Pos start, Pos end, Function<Pos, T> i) {
        List<T> d = new ArrayList<>();
        double topBlockX = (start.x() < end.x() ? end.x() : start.x());
        double bottomBlockX = (start.x() > end.x() ? end.x() : start.x());

        double topBlockY = (start.y() < end.y() ? end.y() : start.y());
        double bottomBlockY = (start.y() > end.y() ? end.y() : start.y());

        double topBlockZ = (start.z() < end.z() ? end.z() : start.z());
        double bottomBlockZ = (start.z() > end.z() ? end.z() : start.z());

        for (double x = bottomBlockX; x <= topBlockX; x++) {
            for (double z = bottomBlockZ; z <= topBlockZ; z++) {
                for (double y = bottomBlockY; y <= topBlockY; y++) {
                    d.add((T) i.apply(new Pos(x, y, z)));
                }
            }
        }
        return d;
    }

}
