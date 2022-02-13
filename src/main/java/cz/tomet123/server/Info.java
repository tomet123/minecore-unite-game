package cz.tomet123.server;

import lombok.extern.slf4j.Slf4j;
import net.minestom.server.MinecraftServer;

@Slf4j
public class Info {
    public static void printVersionLines() {
        log.info("====== "+Versions.NAME+" =====");
        log.info("Java: " + Runtime.version());
        log.info("Version: " + Versions.VERSION);
        log.info("Minestom: " + Versions.MINESTOM_VERSION);
        log.info("Protocol: %d (%s)".formatted(MinecraftServer.PROTOCOL_VERSION, MinecraftServer.VERSION_NAME));
        log.info("================================");
    }

}
