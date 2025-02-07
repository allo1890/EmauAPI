package fr.mathip.azplugin.bukkit.commands;

import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import fr.mathip.azplugin.bukkit.utils.AZColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AZItemRender implements AZCommand {
    @Override
    public String name() {
        return "itemrender";
    }

    @Override
    public String permission() {
        return "azplugin.command.itemrender";
    }

    @Override
    public String description() {
        return "Change la couleur, la taille et la rareté de l'item porté";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player p;
        if (sender instanceof Player) {
            p = (Player) sender;
        } else {
            sender.sendMessage("§cErreur: Vous devez être un joueur pour exécuter cette commande !");
            return;
        }
        if (p.getItemInHand() == null) {
            p.sendMessage("§cErreur: Vous devez porter un item !");
            return;
        }

        if (args.length >= 3 && args[0].equalsIgnoreCase("itemrender")) {
            try {
                NBTItem nbti = new NBTItem(p.getItemInHand());

                if (args[1].equalsIgnoreCase("rarity")) {
                    nbti.mergeCompound(new NBTContainer("{PacRender:{Rarity:\"" + args[2] + "\"},PacDisplay:{Rarity:\"" + args[2] + "\"}}"));
                    p.sendMessage("§aRareté définie sur : " + args[2]);
                } else {
                    nbti.mergeCompound(new NBTContainer("{PacRender: {Scale: " + Float.parseFloat(args[1]) + ", Color: " + AZColor.get0xAARRGGBB(args[2]) + "}, PacDisplay: {Color: " + AZColor.get0xAARRGGBB(args[2]) + "}}"));
                    p.sendMessage("§aTaille et couleur mises à jour !");
                }

                p.getItemInHand().setItemMeta(nbti.getItem().getItemMeta());
            } catch (NumberFormatException e) {
                p.sendMessage("§cErreur : La valeur est invalide !");
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("itemrender")) {
            try {
                if (args[1].equalsIgnoreCase("rarity")) {
                    p.sendMessage("§cErreur: Veuillez spécifier une rareté !");
                } else {
                    NBTItem nbti = new NBTItem(p.getItemInHand());
                    nbti.mergeCompound(new NBTContainer("{PacRender: {Scale: " + Float.parseFloat(args[1]) + "}}"));
                    p.getItemInHand().setItemMeta(nbti.getItem().getItemMeta());
                    p.sendMessage("§aTaille mise à jour !");
                }
            } catch (NumberFormatException e) {
                p.sendMessage("§cErreur : La valeur est invalide !");
            }
        } else {
            p.sendMessage("§c/az itemrender <taille> [couleur(Hex)]");
            p.sendMessage("§c/az itemrender rarity <nom_de_rareté>");
            p.sendMessage("§fVous pouvez utiliser ce site pour faire des couleurs en Hexadécimal §ahttps://htmlcolorcodes.com/fr/");
        }
    }
}
