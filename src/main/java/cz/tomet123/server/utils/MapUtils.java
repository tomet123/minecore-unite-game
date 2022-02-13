package cz.tomet123.server.utils;

import cz.tomet123.server.Storage.MapUniq;
import cz.tomet123.server.pojo.MapPos;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.other.ItemFrameMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.metadata.MapMeta;

import java.util.ArrayList;
import java.util.List;


public class MapUtils {

    public static List<MapPos> generateMapPos(Pos lLeftCorner, Pos hRightCorner) {
        List<MapPos> d = new ArrayList<>();
        int xm = 0, ym = 0, zm = 0; // pocitadlo pixelu
        double xr, yr, zr; //rotace +-
        if (lLeftCorner.x() <= hRightCorner.x()) xr = 1;
        else xr = -1;
        if (lLeftCorner.z() <= hRightCorner.z()) zr = 1;
        else zr = -1;
        if (lLeftCorner.y() <= hRightCorner.y()) yr = 1;
        else yr = -1;

        for (double x = lLeftCorner.x(); x != hRightCorner.x() + xr; x = x + xr) {
            if (x == lLeftCorner.x()) xm = 0;
            for (double z = lLeftCorner.z(); z != hRightCorner.z() + zr; z = z + zr) {
                if (z == lLeftCorner.z()) zm = 0;
                for (double y = lLeftCorner.y(); y != hRightCorner.y() + yr; y = y + yr) {
                    if (y == lLeftCorner.y()) ym = 0;
                    int id = MapUniq.getMapId();
                    d.add(new MapPos(new Pos(x, y, z), id, xm, ym, zm));
                    ym += 128;
                }
                zm += 128;
            }
            xm += 128;
        }
        return d;

    }


    public static ItemStack getMap(int mapId) {
        return ItemStack.builder(Material.FILLED_MAP).meta(new MapMeta.Builder().mapId(mapId).build()).amount(1).build();
    }


    public static void CreateItemFrameEntity(Instance i, ItemStack itemStack, Pos pos, ItemFrameMeta.Orientation rotation, float yaw, float pitch, boolean spawn) {
        Entity itemFrame = new Entity(EntityType.ITEM_FRAME);
        ItemFrameMeta meta = (ItemFrameMeta) itemFrame.getEntityMeta();
        meta.setOrientation(rotation);
        meta.setItem(itemStack);
        pos = pos.withYaw(yaw);
        pos = pos.withPitch(pitch);
        itemFrame.setInstance(i, pos);
        if (spawn) itemFrame.spawn();

    }
}
