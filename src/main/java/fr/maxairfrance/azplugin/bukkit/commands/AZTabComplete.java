package fr.maxairfrance.azplugin.bukkit.commands;

import fr.maxairfrance.azplugin.bukkit.config.PopupConfig;
import fr.maxairfrance.azplugin.bukkit.handlers.PLSPPlayerModel;
import fr.maxairfrance.azplugin.bukkit.packets.PacketPopup;
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
                CommandManager commandManager = CommandManager.getInstance();
                if (commandManager == null || commandManager.getCommands() == null) {
                    return Collections.emptyList();
                }
                List<String> completion = new ArrayList<>();
                for (AZCommand azCommand : commandManager.getCommands().values()) {
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
                        if (args.length == 2) {
                            for (PLSPPlayerModel plspPlayerModel : PLSPPlayerModel.values()) {
                                completion.add(plspPlayerModel.name());
                            }
                        } else if (args.length == 3) {
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                completion.add(player.getName());
                            }
                        }
                        break;

                    case "worldenv":
                        if (args.length == 2) {
                            completion.add("NETHER");
                            completion.add("NORMAL");
                            completion.add("THE_END");
                        } else if (args.length == 3) {
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                completion.add(player.getName());
                            }
                        }
                        break;

                    case "summon":
                        if (args.length == 2) {
                            for (EntityType entityType : EntityType.values()) {
                                completion.add(entityType.name());
                            }
                                } else if (args.length == 3) {
                                    completion.add("1");
                                    completion.add("2");
                                }
                        break;

                    case "popup":
                        if (args.length == 2) {
                            PopupConfig popupConfig = PopupConfig.getInstance();
                            if (popupConfig != null && popupConfig.popups != null) {
                                for (PacketPopup popup : popupConfig.popups) {
                                    if (popup != null && popup.getName().toLowerCase().startsWith(argLower)) {
                                        completion.add(popup.getName());
                                    }
                                }
                            }
                        } else if (args.length == 3) {
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                if (player.getName().toLowerCase().startsWith(args[2].toLowerCase())) {
                                    completion.add(player.getName());
                                }
                            }
                        }
                        break;

                    case "seechunks":
                        if (args.length == 2) {
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                if (player.getName().toLowerCase().startsWith(argLower)) {
                                    completion.add(player.getName());
                                }
                            }
                        } else if (args.length == 3) {
                            completion.add("on");
                            completion.add("off");
                        }
                        break;

                    case "subtag":
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

                    case "tag":
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
                            completion.add("1");
                            completion.add("1.5");
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
                                completion.add("#00FF08");
                                completion.add("#00FFE4");
                            }
                        }
                        break;
                    case "size":
                        if (args.length == 2) {
                            completion.add("0.5");
                            completion.add("1.0");
                            completion.add("2.0");
                        } else if (args.length == 3) {
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                if (player.getName().toLowerCase().startsWith(args[2].toLowerCase())) {
                                    completion.add(player.getName());
                                }
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