package fr.mathip.azplugin.bukkit.packets;

import fr.mathip.azplugin.bukkit.AZPlugin;
import fr.mathip.azplugin.bukkit.AZManager;
import org.bukkit.entity.Player;
import pactify.client.api.plsp.packet.client.PLSPPacketVignette;

public class PacketVignette {
    public PacketVignette() {
    }

    public static void setVignette(Player player, Integer red, Integer green, Integer blue) {
        float redFloat = (float)red / 255.0F;
        float greenFloat = (float)green / 255.0F;
        float blueFloat = (float)blue / 255.0F;
        AZPlugin.getAZManager();
        AZManager.sendPLSPMessage(player, new PLSPPacketVignette(true, redFloat, greenFloat, blueFloat));
    }

    public static void resetVignette(Player player) {
        AZPlugin.getAZManager();
        AZManager.sendPLSPMessage(player, new PLSPPacketVignette(false, 0.0F, 0.0F, 0.0F));
    }
}
