package fr.maxairfrance.azplugin.bukkit.commands;

import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.*;

@Getter
public class CommandManager implements CommandExecutor {

    private final Map<Class<? extends AZCommand>, AZCommand> commands;
    @Getter
    private static CommandManager instance;
    public CommandManager() {
        commands = new HashMap<>();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        instance = this;
        if (s.equalsIgnoreCase("az")) {
            if (args.length == 0) {
                commandSender.sendMessage("§a[§2EmauPlugin§a]§f Liste des commandes:");
                for (AZCommand azCommand : commands.values()) {
                    commandSender.sendMessage("§a /az " + azCommand.name() + " :§f " + azCommand.description());
                }
                return true;
            }
            for (Map.Entry<Class<? extends AZCommand>, AZCommand> azCommand : commands.entrySet()) {
                if (args[0].equalsIgnoreCase(azCommand.getValue().name())) {
                    if (!commandSender.hasPermission(azCommand.getValue().permission())) {
                        commandSender.sendMessage("§cErreur: Vous n'avez pas la permission d'utiliser cette commande !");
                        return true;
                    }
                    azCommand.getValue().execute(commandSender, args);
                    return true;
                }
            }
            commandSender.sendMessage("§cCommand inconnu !");
            return true;
        }
        return false;
    }

    public void addCommand(AZCommand azCommand) {
        commands.put(azCommand.getClass(), azCommand);
    }
}
