package fr.maxairfrance.azplugin.bukkit.packets;

import lombok.Getter;

@Getter
public class PacketUiComponent {

    private final String text;
    private final String name;
    private final String hoverText;
    private final String command;

    public PacketUiComponent(String text, String name, String hoverText, String command) {
        this.text = text;
        this.name = name;
        this.hoverText = hoverText;
        this.command = command;
    }
}