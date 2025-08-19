package fr.maxairfrance.azplugin.bukkit;

import fr.maxairfrance.azplugin.bukkit.utils.ConcretePLSPPacketBuffer;
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
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import pactify.client.api.mcprotocol.util.NotchianPacketUtil;
import pactify.client.api.plsp.PLSPPacket;
import pactify.client.api.plsp.PLSPPacketHandler;
import pactify.client.api.plsp.PLSPProtocol;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AZManager implements Listener, Closeable {

    private static final Pattern AZ_HOSTNAME_PATTERN = Pattern.compile("[\u0000\u0002]PAC([0-9A-F]{5})[\u0000\u0002]");

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

        if (!isValidLauncher(event.getPlayer())) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "AZLauncher requis - Telecharger sur https://az-launcher.nz/fr/");
            return;
        }

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
        if (AZPlayer != null) {
            AZPlayer.join();
        }
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

    private boolean isValidLauncher(Player player) {
        try {
            final List<MetadataValue> hostnameMeta = player.getMetadata("AZPlugin:hostname");
            if (hostnameMeta.isEmpty()) return false;

            final String hostname = hostnameMeta.get(0).asString();
            final Matcher matcher = AZ_HOSTNAME_PATTERN.matcher(hostname);

            if (matcher.find()) {
                int version = Integer.parseInt(matcher.group(1), 16);
                return version == 16;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static void sendPLSPMessage(Player player, PLSPPacket<PLSPPacketHandler.ClientHandler> message) {
        if (!AZPlayer.hasAZLauncher(player)) {
            return;
        }

        try {
            PLSPPacketBuffer buf = new ConcretePLSPPacketBuffer();
            PLSPProtocol.PacketData<?> packetData = PLSPProtocol.getClientPacketByClass(message.getClass());
            NotchianPacketUtil.writeString(buf, packetData.getId(), 32767);
            message.write(buf);
            player.sendPluginMessage(EmauAPI.getInstance(), "PLSP", buf.toBytes());
        } catch (Exception e) {
            EmauAPI.getInstance().getLogger().log(Level.WARNING, "Exception sending PLSP message to " + ((player != null) ? player.getName() : "null") + ":", e);
        }
    }

    public void close() throws IOException {
        HandlerList.unregisterAll(this);
        this.plugin.getServer().getMessenger().unregisterOutgoingPluginChannel(this.plugin, "PLSP");
    }
}