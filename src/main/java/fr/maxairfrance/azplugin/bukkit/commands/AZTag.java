package fr.maxairfrance.azplugin.bukkit.commands;

import fr.maxairfrance.azplugin.bukkit.AZPlugin;
import fr.maxairfrance.azplugin.bukkit.AZPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pactify.client.api.plprotocol.metadata.ImmutablePactifyTagMetadata;
import pactify.client.api.plprotocol.metadata.PactifyTagMetadata;

public class AZTag implements AZCommand {

    @Override
    public String name() {
        return "tag";
    }

    @Override
    public String permission() {
        return "azplugin.command.tag";
    }

    @Override
    public String description() {
        return "Change le pseudo d'un joueur";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player target;
        if (args.length >= 3) {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage("§cCe joueur est hors-ligne !");
                return;
            }
        } else {
            sender.sendMessage("§cUsage: /az tag <joueur> <tag>");
            return;
        }
        AZPlayer azPlayer = AZPlugin.getAZManager().getPlayer(target);
        if (args[2].equalsIgnoreCase("reset")) {
            azPlayer.getPlayerMeta().setTag(new ImmutablePactifyTagMetadata(""));
            azPlayer.updateMeta();
            sender.sendMessage("§a[§2EmauTag§a]§f Changement de tag pour le joueur §b" + target.getName() + " §feffectué !");
            return;
        }
        StringBuilder sb = new StringBuilder();
        int count = 0;
        sb.append(args[2]);
        for (String arg : args) {
            if (count > 2) {
                sb.append(" ").append(arg);
            }
            count++;
        }
        PactifyTagMetadata tagMetadata = new PactifyTagMetadata();
        tagMetadata.setText(sb.toString());
        azPlayer.getPlayerMeta().setTag(tagMetadata);
        Bukkit.getScheduler().runTaskAsynchronously(AZPlugin.getInstance(), azPlayer::updateMeta);
        sender.sendMessage("§a[§2EmauTag§a]§f Changement de tag pour le joueur §b" + target.getName() + " §feffectué !");
    }
}
