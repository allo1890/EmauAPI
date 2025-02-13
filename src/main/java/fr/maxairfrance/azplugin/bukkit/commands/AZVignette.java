package fr.maxairfrance.azplugin.bukkit.commands;

import fr.maxairfrance.azplugin.bukkit.packets.PacketVignette;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        return "Change l'environnement d'un joueur avec un effet de vignette coloré.";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(this.permission())) {
            sender.sendMessage("§a[§2EmauPlugin§a]§f Vous n'avez pas la permission d'utiliser cette commande !");
            return;
        }

        if (args.length < 4) {
            sender.sendMessage("§a[§2EmauPlugin§a]§f Usage: /az vignette <red> <green> <blue> [player]");
            return;
        }

        Player target = args.length >= 5 ? Bukkit.getPlayer(args[4]) : (sender instanceof Player ? (Player) sender : null);

        if (target == null) {
            sender.sendMessage("§a[§2EmauVignette§a]§f Le joueur est hors-ligne !");
            return;
        }

        try {
            int red = Integer.parseInt(args[1]);
            int green = Integer.parseInt(args[2]);
            int blue = Integer.parseInt(args[3]);

            if (isValidColor(red) || isValidColor(green) || isValidColor(blue)) {
                sender.sendMessage("§a[§2EmauVignette§a]§f Les valeurs de couleur doivent être comprises entre 0 et 255 !");
                return;
            }

            PacketVignette.setVignette(target, red, green, blue);
            sender.sendMessage("§a[§2EmauVignette§a]§f Changement d'environnement effectué pour le joueur §b" + target.getName() + " §f!");
        } catch (NumberFormatException e) {
            sender.sendMessage("§a[§2EmauVignette§a]§f Erreur: Veuillez entrer des nombres valides entre 0 et 255 pour les couleurs !");
        }
    }

    private boolean isValidColor(int value) {
        return value >= 0 && value <= 255;
    }
}
