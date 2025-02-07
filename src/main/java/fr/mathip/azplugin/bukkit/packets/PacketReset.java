package fr.mathip.azplugin.bukkit.packets;

import fr.mathip.azplugin.bukkit.AZPlugin;
import org.bukkit.entity.Player;
import pactify.client.api.plsp.packet.client.PLSPPacketReset;

public class PacketReset {
    private static final fr.mathip.azplugin.bukkit.AZManager AZManager = AZPlugin.getAZManager();

    public static void reset(Player player) {
        fr.mathip.azplugin.bukkit.AZManager.sendPLSPMessage(player, new PLSPPacketReset());
    }
}
