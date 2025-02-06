// Celui-ci fonctionne sous BungeeCord

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
        Player target;

        if (args.length >= 5) {
            target = Bukkit.getPlayer(args[4]);
            if (target == null) {
                sender.sendMessage("§cCe joueur est hors-ligne !");
                return;
            }
        } else {
            if (sender instanceof Player) {
                target = (Player) sender;
            } else {
                sender.sendMessage("§cErreur: Vous devez être un joueur pour exécuter cette commande");
                return;
            }
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
                AZManager.sendPLSPMessage(target, packetVignette);
                sender.sendMessage("§a[AZPlugin]§e Changement d'environnement effectué");

            } catch (NumberFormatException e) {
                sender.sendMessage("§cErreur: Les valeurs RGB sont invalides. Assurez-vous de fournir des nombres.");
            }
        }
        else if (args.length == 3 && args[2].equalsIgnoreCase("reset")) {
            PLSPPacketVignette packetVignette = new PLSPPacketVignette();
            packetVignette.setEnabled(false);
            AZManager.sendPLSPMessage(target, packetVignette);
            sender.sendMessage("§a[AZPlugin]§e Vignette réinitialisée pour " + target.getName());
        }
        else {
            sender.sendMessage("§cUsage: /az vignette <R> <G> <B> [joueur]");
            sender.sendMessage("§aVous pouvez utiliser ce site pour faire des couleurs RGB : https://htmlcolorcodes.com/fr/");
        }
    }
}
