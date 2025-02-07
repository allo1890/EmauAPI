package fr.mathip.azplugin.bukkit.commands;

import fr.mathip.azplugin.bukkit.AZManager;
import fr.mathip.azplugin.bukkit.AZPlugin;
import fr.mathip.azplugin.bukkit.handlers.PLSPWorldEnv;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pactify.client.api.plsp.packet.client.PLSPPacketWorldEnv;

public class AZWorldEnv implements AZCommand {

    @Override
    public String name() {
        return "worldenv";
    }

    @Override
    public String permission() {
        return "azplugin.command.worldenv";
    }

    @Override
    public String description() {
        return "Change le ciel d'un joueur";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cErreur: Vous avez mal utiliser la commande. Usage: /worldenv <type> [joueur]");
            return;
        }

        PLSPWorldEnv worldEnv;
        try {
            worldEnv = PLSPWorldEnv.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage("§cType d'environnement invalide. Vérifiez les valeurs disponibles.");
            return;
        }

        Player target;
        if (args.length >= 3) {
            target = Bukkit.getPlayer(args[2]);
            if (target == null) {
                sender.sendMessage("§cCe joueur est hors-ligne !");
                return;
            }
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cErreur: Vous devez être un joueur pour exécuter cette commande.");
                return;
            }
            target = (Player) sender;
        }

        AZPlugin.getAZManager().getPlayer(target);

        PLSPPacketWorldEnv worldEnvPacket = new PLSPPacketWorldEnv();
        worldEnvPacket.setName(target.getWorld().getName());
        worldEnvPacket.setType(worldEnv.name());

        AZManager.sendPLSPMessage(target, worldEnvPacket);
        sender.sendMessage("§a[§2EmauWorldEnv] §fChangement de l'environnement de §6" + target.getName() + " §fvers §6" + worldEnv.name() + " §f!");
    }
}
