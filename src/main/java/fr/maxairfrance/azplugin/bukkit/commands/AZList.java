package fr.maxairfrance.azplugin.bukkit.commands;

import fr.maxairfrance.azplugin.bukkit.AZPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AZList implements AZCommand {

    @Override
    public String name() {
        return "list";
    }

    @Override
    public String permission() {
        return "emauapi.command.list";
    }

    @Override
    public String description() {
        return "Obtient la liste des joueurs qui sont sur le AZ Launcher ou non";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        List<String> pactifyList = new ArrayList<>();
        List<String> vanillaList = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (AZPlayer.hasAZLauncher(player)) {
                pactifyList.add(player.getName());
                continue;
            }
            vanillaList.add(player.getName());
        }
        pactifyList.sort(String::compareToIgnoreCase);
        vanillaList.sort(String::compareToIgnoreCase);
        sender.sendMessage("§fLes joueurs qui utilisent le AZ launcher: " + (
                pactifyList.isEmpty() ? ("§7(Aucun)") : ("§a" + String.join(", ", pactifyList))));
        sender.sendMessage("§fLes joueurs qui n'utilisent pas le AZ launcher: " + (
                vanillaList.isEmpty() ? ("§7(Aucun)") : ("§c" + String.join(", ", vanillaList))));

    }
}
