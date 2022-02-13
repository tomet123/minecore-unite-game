package cz.tomet123.server.event;

import cz.tomet123.server.Server;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.player.PlayerPacketEvent;
import net.minestom.server.event.trait.InventoryEvent;
import net.minestom.server.event.trait.PlayerEvent;
import net.minestom.server.network.packet.client.play.ClientHeldItemChangePacket;
import net.minestom.server.network.packet.client.play.ClientPlayerDiggingPacket;

public class EventImpl {


    public static EventImpl event = null;

    EventNode<InventoryEvent> inventoryNode = EventNode.type("inv-listener", EventFilter.INVENTORY);
    EventNode<PlayerEvent> playerNode = EventNode.type("player-listener", EventFilter.PLAYER);


    public EventImpl() {
        inventoryNode.setPriority(500);
        playerNode.setPriority(500);

        if(!Server.DEV)inventoryNode.addListener(InventoryPreClickEvent.class, this::inventoryPreClickEvent);
        if(!Server.DEV)playerNode.addListener(PlayerPacketEvent.class, this::playerPacketEvent);

        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addChild(playerNode);
        globalEventHandler.addChild(inventoryNode);
    }

    public static void inicialize() {
        event = new EventImpl();
    }

    public void inventoryPreClickEvent(InventoryPreClickEvent inventoryPreClickEvent) {
        if (!Server.DEV) inventoryPreClickEvent.setCancelled(true);
    }

    public void playerPacketEvent(PlayerPacketEvent playerPacketEvent) {

        if (playerPacketEvent.getPacket() instanceof ClientPlayerDiggingPacket packet) {
            if (packet.status().equals(ClientPlayerDiggingPacket.Status.DROP_ITEM) || packet.status().equals(ClientPlayerDiggingPacket.Status.DROP_ITEM_STACK)) {
                playerPacketEvent.getPlayer().setItemInMainHand(playerPacketEvent.getPlayer().getItemInMainHand());
                playerPacketEvent.setCancelled(true);
            }
        } else if (playerPacketEvent.getPacket() instanceof ClientHeldItemChangePacket packet) {
            if (packet.slot() > 0) {
                playerPacketEvent.getPlayer().setHeldItemSlot((byte) 0); //TODO change based on kit
                playerPacketEvent.setCancelled(true);
            }
        }

        // System.out.println(playerPacketEvent.getPacket());

    }


}
