package fr.maxairfrance.azplugin.bukkit.packets;

import fr.maxairfrance.azplugin.bukkit.AZPlugin;
import org.bukkit.entity.Player;
import pactify.client.api.plsp.packet.client.PLSPPacketReset;

public class PacketReset {
    private static final fr.maxairfrance.azplugin.bukkit.AZManager AZManager = AZPlugin.getAZManager();

    public static void reset(Player player) {
        fr.maxairfrance.azplugin.bukkit.AZManager.sendPLSPMessage(player, new PLSPPacketReset());
    }
}
