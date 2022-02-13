package cz.tomet123.server;

import cz.tomet123.server.Provider.GamePlayer;
import cz.tomet123.server.command.dev.GenerateChunkJsonCommand;
import cz.tomet123.server.command.dev.TestLevelCommand;
import cz.tomet123.server.command.dev.TestScoreCommand;
import cz.tomet123.server.event.EventImpl;
import cz.tomet123.server.map.LobbyMapMonitor;
import cz.tomet123.server.world.SpawnGenerator;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.extras.optifine.OptifineSupport;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.DimensionType;

public class Server {


    private static final NamespaceID NO_LIGHT_MGMT_DIMENSION_ID = NamespaceID.from("gauntlet:empty_instance");
    public static boolean DEV = true;
    private final InstanceManager instanceManager;
    private final MinecraftServer minecraftServer;


    private InstanceContainer lobbyInstance;
    private InstanceContainer startingInstance;
    private InstanceContainer gameInstance;

    private LobbyMapMonitor lobbymap;

    public Server() {
        minecraftServer = MinecraftServer.init();
        instanceManager = MinecraftServer.getInstanceManager();

        serverOptions();
        initLobby();
        if (DEV) dev();
        events();
        EventImpl.inicialize();

        minecraftServer.start("0.0.0.0", 25565);

        LobbyMapMonitor.inicialize(lobbyInstance, "lobby-main");

    }

    private DimensionType generateFullLightDim() {

        if (!MinecraftServer.getDimensionTypeManager().isRegistered(NO_LIGHT_MGMT_DIMENSION_ID)) {
            DimensionType dimensionType = DimensionType.builder(NO_LIGHT_MGMT_DIMENSION_ID).ambientLight(1.0f).build();
            MinecraftServer.getDimensionTypeManager().addDimension(dimensionType);
        }
        return MinecraftServer.getDimensionTypeManager().getDimension(NO_LIGHT_MGMT_DIMENSION_ID);
    }

    private void initLobby() {
        lobbyInstance = instanceManager.createInstanceContainer(generateFullLightDim());
        lobbyInstance.setChunkGenerator(new SpawnGenerator());

    }

    private void serverOptions() {
        OptifineSupport.enable();
        PlacementRules.init();
        MinecraftServer.getConnectionManager().setPlayerProvider(GamePlayer::new);

    }

    private void events() {

        EventNode<PlayerEvent> playerNode = EventNode.type("player-listener", EventFilter.PLAYER);
        playerNode.setPriority(500);
        playerNode.addListener(PlayerBlockBreakEvent.class, playerBlockBreakEvent -> {
            if (!DEV) playerBlockBreakEvent.setCancelled(true);
        });
        playerNode.addListener(PlayerBlockPlaceEvent.class, playerBlockPlaceEvent -> {
            if (!DEV) playerBlockPlaceEvent.setCancelled(true);
        });

        playerNode.addListener(PlayerLoginEvent.class, event -> {
            final Player player = event.getPlayer();
            event.setSpawningInstance(lobbyInstance);
            player.setRespawnPoint(new Pos(Chunk.CHUNK_SIZE_X / 2, 42, Chunk.CHUNK_SIZE_Z / 2));
            if(!Server.DEV)player.setGameMode(GameMode.SURVIVAL);
            else player.setGameMode(GameMode.CREATIVE);
            player.setItemInMainHand(ItemStack.of(Material.STONE, 100));
        });

        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addChild(playerNode);

    }

    private void dev() {

        MinecraftServer.getCommandManager().register(new GenerateChunkJsonCommand());
        MinecraftServer.getCommandManager().register(new TestLevelCommand());
        MinecraftServer.getCommandManager().register(new TestScoreCommand());

    }
}
