package cz.tomet123.server.utils.map;

import com.google.common.collect.Sets;
import cz.tomet123.server.utils.storage.MapUniq;
import cz.tomet123.server.utils.pojo.MapPosition;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.other.ItemFrameMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.metadata.MapMeta;
import net.minestom.server.map.framebuffers.LargeGraphics2DFramebuffer;
import net.minestom.server.network.packet.server.play.MapDataPacket;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.utils.time.TimeUnit;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
public abstract class BaseMapMonitor {

    protected LargeGraphics2DFramebuffer framebuffer;
    protected List<MapPosition> mpos = null;
    protected List<MapDataPacket> mdpOld = null;
    protected List<MapDataPacket> mdpBeforeSend = null;
    protected Collection<Player> plOld =  new ArrayList<>();

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
    private long repeatms = 200;

    private static void tick(BaseMapMonitor map) {

        Graphics2D renderer = map.framebuffer.getRenderer();
        map.renderer(renderer);

        map.send();
    }


    private Pos setPosYawPitch(ItemFrameMeta.Orientation rotation,Pos pos){
        switch (rotation){
            case UP ->{
                pos=pos.withView(0,-90);
            }
            case DOWN -> {
                pos=pos.withView(0,90);
            }
            case EAST -> {
                pos=pos.withView(-90,0);
            }
            case WEST -> {
                pos=pos.withView(90,0);
            }
            case NORTH -> {
                pos=pos.withView(180,0);
            }
            case SOUTH -> {
                pos=pos.withView(0,0);
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
        else throw new RuntimeException("Invalid map pos;");
    }

    private void send() {
        generateRotation();
        List<MapDataPacket> mdp = mpos.stream().map(mapPosition1 ->
                framebuffer.createSubView(mapPosition1.getLeft(rotation), mapPosition1.getTop()).preparePacket(mapPosition1.getMapId())).toList();
        Collection<Player> pl = MinecraftServer.getConnectionManager().getOnlinePlayers();
        boolean playerChanged=false;
        if(plOld!=null){
            pl =pl.stream().filter(player ->player.getLatency()!=0).toList();
            playerChanged =!(pl.containsAll(plOld) && plOld.containsAll(pl));
        }

        if(mdpOld!=null && !playerChanged){
            log.info("mpd before filter "+mdp.size());
            mdpBeforeSend = mdp.stream().filter(mapDataPacket -> {
                MapDataPacket.ColorContent c = mapDataPacket.colorContent();
                int i = mapDataPacket.mapId();
                return !(mdpOld.stream().filter(m -> m.mapId()==i).filter(x -> Arrays.equals(x.colorContent().data(), c.data())).count() == 1);
            }).toList();
            log.info("mpd after filter "+mdpBeforeSend.size());
        }else {
            mdpBeforeSend=mdp;
        }
        pl.forEach(p -> mdpBeforeSend.forEach(mapDataPacket -> p.getPlayerConnection().sendPacket(mapDataPacket)));
        mdpOld= new ArrayList<>(mdp);
        if(playerChanged)plOld=new ArrayList<>(pl);
        mdpBeforeSend=null;
    }

    protected abstract void renderer(Graphics2D renderer);


}
