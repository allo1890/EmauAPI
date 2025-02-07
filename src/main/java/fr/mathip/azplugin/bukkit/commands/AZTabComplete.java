package fr.mathip.azplugin.bukkit.commands;

import fr.mathip.azplugin.bukkit.config.PopupConfig;
import fr.mathip.azplugin.bukkit.handlers.PLSPPlayerModel;
import fr.mathip.azplugin.bukkit.handlers.PLSPWorldEnv;
import fr.mathip.azplugin.bukkit.packets.PacketPopup;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AZTabComplete implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        if (s.equalsIgnoreCase("az") && commandSender.hasPermission("azplugin.*")) {
            if (args.length == 1) {
                List<String> completion = new ArrayList<>();
                for (AZCommand azCommand : CommandManager.getInstance().getCommands().values()) {
                    if (azCommand.name().toLowerCase().startsWith(args[0].toLowerCase())) {
                        completion.add(azCommand.name());
                    }
                }
                return completion.isEmpty() ? Collections.emptyList() : completion;
            }

            if (args.length >= 2) {
                List<String> completion = new ArrayList<>();
                String argLower = args[1].toLowerCase();

                switch (args[0].toLowerCase()) {
                    case "model":
                        for (PLSPPlayerModel plspPlayerModel : PLSPPlayerModel.values()) {
                            if (plspPlayerModel.name().toLowerCase().startsWith(argLower)) {
                                completion.add(plspPlayerModel.name());
                            }
                        }
                        break;

                    case "worldenv":
                        for (PLSPWorldEnv plspWorldEnv : PLSPWorldEnv.values()) {
                            if (plspWorldEnv.name().toLowerCase().startsWith(argLower)) {
                                completion.add(plspWorldEnv.name());
                            }
                        }
                        break;

                    case "summon":
                        for (EntityType entityType : EntityType.values()) {
                            if (entityType.name().toLowerCase().startsWith(argLower)) {
                                completion.add(entityType.name());
                            }
                        }
                        break;

                    case "popup":
                        for (PacketPopup popup : PopupConfig.getInstance().popups) {
                            if (popup.getName().toLowerCase().startsWith(argLower)) {
                                completion.add(popup.getName());
                            }
                        }
                        break;

                    case "suptag":
                        if (args.length == 2) {
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                if (player.getName().toLowerCase().startsWith(argLower)) {
                                    completion.add(player.getName());
                                }
                            }
                        } else if (args.length == 3) {
                            completion.add("reset");
                        }
                        break;

                    case "itemrender":
                        if (args.length == 2) {
                            completion.add("rarity");
                            completion.add("taille");
                        } else if (args.length == 3) {
                            if (args[1].equalsIgnoreCase("rarity")) {
                                completion.add("AUTO");
                                completion.add("NONE");
                                completion.add("UNCOMMON");
                                completion.add("RARE");
                                completion.add("EPIC");
                                completion.add("LEGENDARY");
                                completion.add("MYTHIC");
                            } else {
                                completion.add("#FF0000");
                            }
                        }
                        break;
                }
                return completion.isEmpty() ? Collections.emptyList() : completion;
            }
        }
        return Collections.emptyList();
    }
}
