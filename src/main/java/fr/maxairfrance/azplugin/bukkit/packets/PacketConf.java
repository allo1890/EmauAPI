package fr.maxairfrance.azplugin.bukkit.packets;

import fr.maxairfrance.azplugin.bukkit.EmauAPI;
import fr.maxairfrance.azplugin.bukkit.AZManager;
import fr.maxairfrance.azplugin.bukkit.handlers.PLSPConfFlag;
import fr.maxairfrance.azplugin.bukkit.handlers.PLSPConfInt;
import org.bukkit.entity.Player;
import pactify.client.api.plsp.packet.client.PLSPPacketConfFlag;
import pactify.client.api.plsp.packet.client.PLSPPacketConfInt;

public class PacketConf {
    private static final AZManager AZManager = EmauAPI.getAZManager();

    public static void setFlag(Player player, PLSPConfFlag flag, Boolean enabled) {
        PLSPPacketConfFlag PacketConfFlag = new PLSPPacketConfFlag();
        PacketConfFlag.setFlag(flag.name().toLowerCase());
        PacketConfFlag.setEnabled(enabled);

        fr.maxairfrance.azplugin.bukkit.AZManager.sendPLSPMessage(player, PacketConfFlag);
    }

    public static void setInt(Player player, PLSPConfInt param, Integer value) {
        PLSPPacketConfInt PacketConfInt = new PLSPPacketConfInt();
        PacketConfInt.setParam(param.name().toLowerCase());
        PacketConfInt.setValue(value);

        fr.maxairfrance.azplugin.bukkit.AZManager.sendPLSPMessage(player, PacketConfInt);
    }
}
