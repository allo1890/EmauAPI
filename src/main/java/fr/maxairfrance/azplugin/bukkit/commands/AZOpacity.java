package fr.maxairfrance.azplugin.bukkit.commands;

import fr.maxairfrance.azplugin.bukkit.AZPlugin;
import fr.maxairfrance.azplugin.bukkit.AZPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AZOpacity implements AZCommand {

    @Override
    public String name() {
        return "opacity";
    }

    @Override
    public String permission() {
        return "azplugin.command.opacity";
    }

    @Override
    public String description() {
        return "Change l'opacité du joueur";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cErreur : Vous devez spécifier un niveau d'opacité !");
            sender.sendMessage("§cUsage: /opacity <opacité> [joueur]");
            return;
        }

        Player target;
        float opacity;
        try {
            opacity = Float.parseFloat(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cErreur : L'opacité n'est pas un nombre valide. Utilisez un nombre entre 0 et 1.");
            return;
        }

        if (opacity < -1 || opacity > 1) {
            sender.sendMessage("§cErreur : L'opacité doit être comprise entre -1 (totalement opaque) et 1 (totalement transparent).");
            return;
        }

        if (args.length >= 3) {
            target = Bukkit.getPlayer(args[2]);
            if (target == null) {
                sender.sendMessage("§cCe joueur est hors-ligne !");
                return;
            }
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cErreur: Vous devez être un joueur pour exécuter cette commande si vous ne spécifiez pas de cible.");
                return;
            }
            target = (Player) sender;
        }

        AZPlayer azPlayer = AZPlugin.getAZManager().getPlayer(target);
        azPlayer.getPlayerMeta().setOpacity(opacity);
        azPlayer.getEntityMeta().setOpacity(opacity);
        azPlayer.updateMeta();

        sender.sendMessage("§a[§2EmauOpacity§a] §fChangement d'opacité  pour le joueur §b" + target.getName() + " §feffectué !");
    }
}
