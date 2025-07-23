package fr.maxairfrance.azplugin.bukkit;

import fr.maxairfrance.azplugin.bukkit.utils.PLSPPacketBuffer;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import pactify.client.api.mcprotocol.util.NotchianPacketUtil;
import pactify.client.api.plsp.PLSPPacket;
import pactify.client.api.plsp.PLSPPacketHandler;
import pactify.client.api.plsp.PLSPProtocol;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class AZManager implements Listener, Closeable {

    @Getter private final Plugin plugin;
    private final Map<UUID, AZPlayer> players;

    public AZManager(final Plugin plugin) {
        this.players = new HashMap<>();
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "PLSP");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(final PlayerLoginEvent event) {

        event.getPlayer().setMetadata("AZPlugin:hostname", new FixedMetadataValue(this.plugin, event.getHostname()));

        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            this.playerQuit(event.getPlayer());
            return;
        }

        final AZPlayer AZPlayer = new AZPlayer(this, event.getPlayer());
        this.players.put(event.getPlayer().getUniqueId(), AZPlayer);
        AZPlayer.init();
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final AZPlayer AZPlayer = this.getPlayer(event.getPlayer());
        AZPlayer.join();
    }

    public AZPlayer getPlayer(final Player player) {
        return this.players.get(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        this.playerQuit(event.getPlayer());
    }

    private void playerQuit(final Player player) {
        final AZPlayer AZPlayer = this.players.remove(player.getUniqueId());
        if (AZPlayer != null) {
            AZPlayer.free();
        }
    }

    public static void sendPLSPMessage(Player player, PLSPPacket<PLSPPacketHandler.ClientHandler> message) {
        try {
            PLSPPacketBuffer buf = new PLSPPacketBuffer();
            PLSPProtocol.PacketData<?> packetData = PLSPProtocol.getClientPacketByClass(message.getClass());
            NotchianPacketUtil.writeString(buf, packetData.getId(), 32767);
            message.write(buf);
            player.sendPluginMessage(AZPlugin.getInstance(), "PLSP", buf.toBytes());
        } catch (Exception e) {
            AZPlugin.getInstance().getLogger().log(Level.WARNING, "Exception sending PLSP message to " + ((player != null) ? player.getName() : "null") + ":", e);
        }
    }

    public void close() throws IOException {
        HandlerList.unregisterAll(this);
        this.plugin.getServer().getMessenger().unregisterOutgoingPluginChannel(this.plugin, "PLSP");
    }
}