package com.example.server;

import com.example.server.command.GamemodeCommand;
import com.example.server.command.GiveCommand;
import com.example.server.command.TeleportCommand;
import com.example.server.world.NoiseGenerator;
import com.example.server.world.StoneGenerator;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.extras.PlacementRules;
import net.minestom.server.extras.optifine.OptifineSupport;
import net.minestom.server.instance.*;
import net.minestom.server.utils.Position;

public class TemplateServer {

    public static void main(String[] args) {
        // Initialization
        MinecraftServer minecraftServer = MinecraftServer.init();

        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        // Create the instance
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();
        // Set the ChunkGenerator
        instanceContainer.setChunkGenerator(new NoiseGenerator());

        // Util options
        OptifineSupport.enable();
        PlacementRules.init();

        // Commands
        registerCommands();

        // Add an event callback to specify the spawning instance (and the spawn position)
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addEventCallback(PlayerLoginEvent.class, event -> {
            final Player player = event.getPlayer();
            event.setSpawningInstance(instanceContainer);
            player.setRespawnPoint(new Position(0, 42, 0));
        });

        // Start the server on port 25565
        minecraftServer.start("0.0.0.0", 25565);
    }

    private static void registerCommands() {
        CommandManager commandManager = MinecraftServer.getCommandManager();
        commandManager.register(new GamemodeCommand());
        commandManager.register(new GiveCommand());
        commandManager.register(new TeleportCommand());
    }
}
