package fr.mathip.azplugin.bukkit;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PacketUiComponent {
    private final String text;
    private final String name;
    private final String hoverText;
    private final String commmand;
}