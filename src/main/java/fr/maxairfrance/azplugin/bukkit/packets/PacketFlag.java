package fr.maxairfrance.azplugin.bukkit.packets;

import fr.maxairfrance.azplugin.bukkit.EmauAPI;
import fr.maxairfrance.azplugin.bukkit.AZManager;
import fr.maxairfrance.azplugin.bukkit.AZPlayer;
import fr.maxairfrance.azplugin.bukkit.handlers.PLSPFlag;
import fr.maxairfrance.azplugin.bukkit.handlers.PLSPConfInt;
import org.bukkit.entity.Player;
import pactify.client.api.plsp.packet.client.PLSPPacketConfFlag;
import pactify.client.api.plsp.packet.client.PLSPPacketConfInt;

public class PacketFlag {
    private static final AZManager AZManager = EmauAPI.getAZManager();

    public static void setFlag(Player player, PLSPFlag flag, Boolean enabled) {
        if (!AZPlayer.hasAZLauncher(player)) {
            return;
        }

        PLSPPacketConfFlag PacketConfFlag = new PLSPPacketConfFlag();
        PacketConfFlag.setFlag(flag.name().toLowerCase());
        PacketConfFlag.setEnabled(enabled);

        fr.maxairfrance.azplugin.bukkit.AZManager.sendPLSPMessage(player, PacketConfFlag);
    }

    public static void setInt(Player player, PLSPConfInt param, Integer value) {
        if (!AZPlayer.hasAZLauncher(player)) {
            return;
        }

        PLSPPacketConfInt PacketConfInt = new PLSPPacketConfInt();
        PacketConfInt.setParam(param.name().toLowerCase());
        PacketConfInt.setValue(value);

        fr.maxairfrance.azplugin.bukkit.AZManager.sendPLSPMessage(player, PacketConfInt);
    }
}