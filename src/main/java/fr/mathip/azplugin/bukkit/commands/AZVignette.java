package fr.mathip.azplugin.bukkit.commands;

import fr.mathip.azplugin.bukkit.AZManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pactify.client.api.plsp.packet.client.PLSPPacketVignette;

public class AZVignette implements AZCommand {

    @Override
    public String name() {
        return "vignette";
    }

    @Override
    public String permission() {
        return "azplugin.command.vignette";
    }

    @Override
    public String description() {
        return "Change l'environnement d'un joueur";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("azplugin.command.vignette")) {
            sender.sendMessage("§a[§2EmauPlugin§a]§f Vous n'avez pas la permission d'utiliser cette commande !");
            return;
        }

        Player p = null;
        if (args.length >= 5) {
            p = Bukkit.getPlayer(args[4]);
            if (p == null) {
                sender.sendMessage("§a[§2EmauPlugin§a]§f Le joueur est hors-ligne !");
                return;
            }
        } else if (sender instanceof Player) {
            p = (Player) sender;
        }

        if (p == null) {
            sender.sendMessage("§cErreur: Vous devez être un joueur pour exécuter cette commande !");
            return;
        }

        if (args.length >= 4) {
            try {
                int red = Integer.parseInt(args[1]);
                int green = Integer.parseInt(args[2]);
                int blue = Integer.parseInt(args[3]);

                if (red < 0 || red > 255 || green < 0 || green > 255 || blue < 0 || blue > 255) {
                    sender.sendMessage("§cErreur: Les valeurs RGB doivent être entre 0 et 255.");
                    return;
                }

                PLSPPacketVignette packetVignette = new PLSPPacketVignette(true, red, green, blue);
                AZManager.sendPLSPMessage(p, packetVignette);
                sender.sendMessage("§a[§2EmauPlugin§a]§f Changement d'environnement effectué !");

            } catch (NumberFormatException e) {
                sender.sendMessage("§cErreur: Les valeurs RGB sont invalides. Assurez-vous de fournir des nombres.");
            }
        } else {
            sender.sendMessage("§cUsage: /az vignette <red> <green> <blue> [player]");
            sender.sendMessage("§fVous pouvez utiliser ce site pour faire des couleurs RGB : §ahttps://htmlcolorcodes.com/fr/");
        }
    }
}