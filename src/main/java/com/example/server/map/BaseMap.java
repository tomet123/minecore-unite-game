package com.example.server.map;

import com.example.server.pojo.MapPos;
import com.example.server.utils.MapUtils;
import lombok.Getter;
import lombok.Setter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.metadata.other.ItemFrameMeta;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.instance.InstanceTickEvent;
import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.map.framebuffers.LargeGraphics2DFramebuffer;
import net.minestom.server.network.packet.server.play.MapDataPacket;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.utils.time.TimeUnit;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;


public abstract class BaseMap {

    protected static BaseMap map;

    protected LargeGraphics2DFramebuffer framebuffer;
    protected List<MapPos> mpos = null;

    protected String name;

    private Instance instance;

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
    private float yaw = 0;
    @Getter
    @Setter
    private float pitch = 0;
    @Getter
    @Setter
    private long repeatms = 50;

    protected BaseMap(Instance i, String name) {
        this.name = name;
        this.instance = i;

    }

    public static BaseMap inicialize(Instance i, String name) {
        EventNode<InstanceEvent> playerNode = EventNode.type(name + "-map-image", EventFilter.INSTANCE);
        playerNode.addListener(InstanceTickEvent.class, it -> {
            if (map == null && i != null) {
                map = new Lobbymap(i, name);
            }
        });
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addChild(playerNode);

        return map;
    }

    private static void tick(BaseMap lobbymap) {

        Graphics2D renderer = lobbymap.framebuffer.getRenderer();
        lobbymap.renderer(renderer);

        send(map);
    }

    private static void send(BaseMap lobbymap) {
        List<MapDataPacket> mdp = lobbymap.mpos.stream().map(mapPos1 ->
                lobbymap.framebuffer.createSubView(mapPos1.getLeft(), mapPos1.getTop()).preparePacket(mapPos1.getMapId())).collect(Collectors.toList());

        MinecraftServer.getConnectionManager().getOnlinePlayers().forEach(p -> {
            mdp.forEach(mapDataPacket -> p.getPlayerConnection().sendPacket(mapDataPacket));
        });
    }

    protected void initInternal() {
        framebuffer = new LargeGraphics2DFramebuffer(128 * getSizeW(), 128 * getSizeH());

        mpos = MapUtils.generateMapPos(getHLeftCorner(), getLRightCorner());
        mpos.forEach(mapPos -> MapUtils.CreateItemFrameEntity(instance, MapUtils.getMap(mapPos.getMapId()), mapPos.getP(), orientation, yaw, pitch, true));

        SchedulerManager scheduler = MinecraftServer.getSchedulerManager();
        scheduler.buildTask(() -> tick(this)).repeat(repeatms, TimeUnit.MILLISECOND).schedule();
    }

    protected abstract void renderer(Graphics2D renderer);


}
