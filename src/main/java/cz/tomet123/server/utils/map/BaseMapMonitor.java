package cz.tomet123.server.utils.map;

import cz.tomet123.server.Storage.MapUniq;
import cz.tomet123.server.utils.pojo.MapPosition;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.other.ItemFrameMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.metadata.MapMeta;
import net.minestom.server.map.framebuffers.LargeGraphics2DFramebuffer;
import net.minestom.server.network.packet.server.play.MapDataPacket;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.utils.time.TimeUnit;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public abstract class BaseMapMonitor {

    protected static BaseMapMonitor map;
    protected LargeGraphics2DFramebuffer framebuffer;
    protected List<MapPosition> mpos = null;
    private Instance instance;
    private MapPosition.rotation rotation = null;
    @Getter
    @Setter
    private Pos HLeftCorner = new Pos(0, 0, 0);
    @Getter
    @Setter
    private Pos LRightCorner = new Pos(0, 0, 0);
    @Getter
    @Setter
    private int SizeW = 0;
    @Getter
    @Setter
    private int SizeH = 0;
    @Getter
    @Setter
    private ItemFrameMeta.Orientation orientation = ItemFrameMeta.Orientation.EAST;
    @Getter
    @Setter
    private long repeatms = 50;

    private static void tick(BaseMapMonitor map) {

        Graphics2D renderer = map.framebuffer.getRenderer();
        map.renderer(renderer);

        map.send();
    }


    private Pos setPosYawPitch(ItemFrameMeta.Orientation rotation,Pos pos){
        switch (rotation){
            case UP ->{
                pos.withYaw(0);
                pos.withPitch(-90);
            }
            case DOWN -> {
                pos.withYaw(0);
                pos.withPitch(90);
            }
            case EAST -> {
                pos.withYaw(-90);
                pos.withPitch(0);
            }
            case WEST -> {
                pos.withYaw(90);
                pos.withPitch(0);
            }
            case NORTH -> {
                pos.withYaw(180);
                pos.withPitch(0);
            }
            case SOUTH -> {
                pos.withYaw(0);
                pos.withPitch(0);
            }
        }
        return pos;
    }

    private List<MapPosition> generateMapPos(Pos lLeftCorner, Pos hRightCorner) {
        List<MapPosition> d = new ArrayList<>();
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
                    d.add(new MapPosition(new Pos(x, y, z), id, xm, ym, zm));
                    ym += 128;
                }
                zm += 128;
            }
            xm += 128;
        }
        return d;

    }

    //TODO compute yaw and pitch
    private void createItemFrameWithMap(Instance i, int mapId, Pos pos, ItemFrameMeta.Orientation rotation) {
        ItemStack map = ItemStack.builder(Material.FILLED_MAP).meta(new MapMeta.Builder().mapId(mapId).build()).amount(1).build();
        Entity itemFrame = new Entity(EntityType.ITEM_FRAME);
        ItemFrameMeta meta = (ItemFrameMeta) itemFrame.getEntityMeta();
        meta.setOrientation(rotation);
        meta.setItem(map);
        pos = setPosYawPitch(rotation,pos);
        itemFrame.setInstance(i, pos);
        itemFrame.spawn();
    }

    public void initMap(Instance instance) {
        this.instance = instance;
        if (instance == null) return;
        framebuffer = new LargeGraphics2DFramebuffer(128 * getSizeW(), 128 * getSizeH());

        mpos = generateMapPos(getHLeftCorner(), getLRightCorner());
        mpos.forEach(mapPosition -> createItemFrameWithMap(instance, mapPosition.getMapId(), mapPosition.getP(), orientation));

        SchedulerManager scheduler = MinecraftServer.getSchedulerManager();
        scheduler.buildTask(() -> tick(this)).repeat(repeatms, TimeUnit.MILLISECOND).schedule();
    }

    private void generateRotation() {
        long x = mpos.stream().map(mapPosition -> mapPosition.getX()).distinct().count();
        long z = mpos.stream().map(mapPosition -> mapPosition.getZ()).distinct().count();
        if (x == 1) rotation = MapPosition.rotation.Z;
        else if (z == 1) rotation = MapPosition.rotation.X;
    }

    private void send() {
        generateRotation();
        List<MapDataPacket> mdp = mpos.stream().map(mapPosition1 ->
                framebuffer.createSubView(mapPosition1.getLeft(rotation), mapPosition1.getTop()).preparePacket(mapPosition1.getMapId())).toList();

        MinecraftServer.getConnectionManager().getOnlinePlayers().forEach(p -> mdp.forEach(mapDataPacket -> p.getPlayerConnection().sendPacket(mapDataPacket)));
    }

    protected abstract void renderer(Graphics2D renderer);


}
