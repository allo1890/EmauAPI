package fr.maxairfrance.azplugin.bukkit.packets;

import fr.maxairfrance.azplugin.bukkit.AZManager;
import fr.maxairfrance.azplugin.bukkit.EmauAPI;
import org.bukkit.entity.Player;
import pactify.client.api.plsp.packet.client.PLSPPacketReset;

public class PacketReset {

    public static void reset(Player player) {
        if (player == null) {
            return;
        }

        AZManager azManager = EmauAPI.getAZManager();
        if (azManager == null) {
            return;
        }

        azManager.sendPLSPMessage(player, new PLSPPacketReset());
    }
}