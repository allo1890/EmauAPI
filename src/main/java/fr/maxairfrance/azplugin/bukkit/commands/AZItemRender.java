package fr.maxairfrance.azplugin.bukkit.commands;

import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import fr.maxairfrance.azplugin.bukkit.utils.AZColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AZItemRender implements AZCommand {

    @Override
    public String name() {
        return "itemrender";
    }

    @Override
    public String permission() {
        return "emauapi.command.itemrender";
    }

    @Override
    public String description() {
        return "Change la couleur, la taille et la rareté de l'item porté";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player;
        if (sender instanceof Player) {
            player = (Player) sender;
        } else {
            sender.sendMessage("§cErreur: Vous devez être un joueur pour exécuter cette commande !");
            return;
        }
        if (player.getItemInHand() == null) {
            player.sendMessage("§cErreur: Vous devez porter un item !");
            return;
        }

        if (args.length >= 3 && args[0].equalsIgnoreCase("itemrender")) {
            try {
                NBTItem nbti = new NBTItem(player.getItemInHand());

                if (args[1].equalsIgnoreCase("rarity")) {
                    nbti.mergeCompound(new NBTContainer("{PacRender:{Rarity:\"" + args[2] + "\"},PacDisplay:{Rarity:\"" + args[2] + "\"}}"));
                    player.sendMessage("§a[§2EmauRarity§a] §fRareté définie sur : " + args[2]);
                } else {
                    try {
                        AZColor.get0xAARRGGBB(args[2]);
                    } catch (IllegalArgumentException e) {
                        player.sendMessage("§cErreur : La couleur Hexadécimale est invalide ! Exemple valide : #FF0000");
                        return;
                    }

                    nbti.mergeCompound(new NBTContainer("{PacRender: {Scale: " + Float.parseFloat(args[1]) + ", Color: " + AZColor.get0xAARRGGBB(args[2]) + "}, PacDisplay: {Color: " + AZColor.get0xAARRGGBB(args[2]) + "}}"));
                    player.sendMessage("§a[§2EmauSizeColor§a] §fTaille et couleur mises à jour !");
                }

                player.getItemInHand().setItemMeta(nbti.getItem().getItemMeta());
            } catch (NumberFormatException e) {
                player.sendMessage("§cErreur : La valeur de taille est invalide !");
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("itemrender")) {
            try {
                if (args[1].equalsIgnoreCase("rarity")) {
                    player.sendMessage("§cErreur: Veuillez spécifier une rareté !");
                } else {
                    NBTItem nbti = new NBTItem(player.getItemInHand());
                    nbti.mergeCompound(new NBTContainer("{PacRender: {Scale: " + Float.parseFloat(args[1]) + "}}"));
                    player.getItemInHand().setItemMeta(nbti.getItem().getItemMeta());
                    player.sendMessage("§a[§2EmauSize§a] §fTaille de l'item mise à jour !");
                }
            } catch (NumberFormatException e) {
                player.sendMessage("§cErreur : La valeur de taille est invalide !");
            }
        } else {
            player.sendMessage("§c/az itemrender <taille> [couleur(Hex)]");
            player.sendMessage("§c/az itemrender rarity <nom_de_rareté>");
            player.sendMessage("§fVous pouvez utiliser ce site pour faire des couleurs en Hexadécimal §bhttps://htmlcolorcodes.com/fr/");
        }
    }
}