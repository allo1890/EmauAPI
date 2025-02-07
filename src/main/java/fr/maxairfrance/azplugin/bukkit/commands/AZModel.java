package fr.maxairfrance.azplugin.bukkit.commands;

import fr.maxairfrance.azplugin.bukkit.AZPlugin;
import fr.maxairfrance.azplugin.bukkit.AZPlayer;
import fr.maxairfrance.azplugin.bukkit.handlers.PLSPPlayerModel;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pactify.client.api.plprotocol.metadata.PactifyModelMetadata;

public class AZModel implements AZCommand{
    @Override
    public String name() {
        return "model";
    }

    @Override
    public String permission() {
        return "azplugin.command.model";
    }

    @Override
    public String description() {
        return "Change l'apparence du joueur (en mob par exemple)";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§c/az model <model> [joueur] [nbt]");
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
            target = (Player) sender;
            if (target == null) {
                sender.sendMessage("§cErreur: Vous devez être un joueur pour executer cette commande");
                return;
            }
        }
        AZPlayer azPlayer = AZPlugin.getAZManager().getPlayer(target);
        if (args[1].equalsIgnoreCase("reset")){
            azPlayer.getPlayerMeta().setModel(new PactifyModelMetadata(-1));
            azPlayer.getEntityMeta().setModel(new PactifyModelMetadata(-1));
            azPlayer.updateMeta();
            sender.sendMessage("§a[§2EmauModel§a]§f Changement de model pour le joueur §b" + target.getName() + " §feffectué !");
            return;
        }

        try {
            PLSPPlayerModel plspPlayerModel = PLSPPlayerModel.valueOf(args[1].toUpperCase());
            PactifyModelMetadata modelMetadata = new PactifyModelMetadata();
            modelMetadata.setId(plspPlayerModel.getId());
            azPlayer.getPlayerMeta().setModel(modelMetadata);
            azPlayer.getEntityMeta().setModel(modelMetadata);
            Bukkit.getScheduler().runTaskAsynchronously(AZPlugin.getInstance(), azPlayer::updateMeta);
            sender.sendMessage("§a[§2EmauModel§a]§f Changement de model pour le joueur §b" + target.getName() + " §feffectué !");
        } catch (IllegalArgumentException e){
            sender.sendMessage("§cErreur : La valeur est invalide !.");
        }

    }
}
