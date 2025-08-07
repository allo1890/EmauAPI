package fr.maxairfrance.azplugin.bukkit.commands;

import fr.maxairfrance.azplugin.bukkit.EmauAPI;
import fr.maxairfrance.azplugin.bukkit.handlers.PLSPFlag;
import fr.maxairfrance.azplugin.bukkit.packets.PacketFlag;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AZSeechunks implements AZCommand {

    @Override
    public String name() {
        return "seechunks";
    }

    @Override
    public String permission() {
        return "azplugin.comand.seechunks";
    }

    @Override
    public String description() {
        return "Active ou non la vision des chunks à un joueur";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length >= 2) {
            if (Bukkit.getPlayer(args[1]) != null) {
                Player target = Bukkit.getPlayer(args[1]);
                if (args.length >= 3) {
                    if (args[2].equalsIgnoreCase("on")) {
                        PacketFlag.setFlag(target, PLSPFlag.SEE_CHUNKS, true);
                    } else if (args[2].equalsIgnoreCase("off")) {
                        PacketFlag.setFlag(target, PLSPFlag.SEE_CHUNKS, false);
                    } else {
                        sender.sendMessage("§c/az seechunks <joueur> [on/off]");
                    }
                    return;
                }
                if (EmauAPI.getInstance().playersSeeChunks.contains(target)) {
                    PacketFlag.setFlag(target, PLSPFlag.SEE_CHUNKS, false);
                    EmauAPI.getInstance().playersSeeChunks.remove(target);
                } else {
                    PacketFlag.setFlag(target, PLSPFlag.SEE_CHUNKS, true);
                    EmauAPI.getInstance().playersSeeChunks.add(target);
                }

            } else {
                sender.sendMessage("§cErreur: Ce joueur est hors-ligne !");
            }
        } else {
            sender.sendMessage("§c/az seechunks <joueur> [on/off]");
        }
    }
}
