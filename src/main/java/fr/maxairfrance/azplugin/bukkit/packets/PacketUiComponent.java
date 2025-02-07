package fr.maxairfrance.azplugin.bukkit.packets;

import lombok.Getter;

@Getter
public class PacketUiComponent {

    private final String text;
    private final String name;
    private final String hoverText;
    private final String commmand;

    public PacketUiComponent(String text, String name, String hoverText, String commmand) {
        this.text = text;
        this.name = name;
        this.hoverText = hoverText;
        this.commmand = commmand;
    }
}