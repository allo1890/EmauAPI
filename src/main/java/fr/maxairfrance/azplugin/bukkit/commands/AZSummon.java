package fr.maxairfrance.azplugin.bukkit.commands;

import fr.maxairfrance.azplugin.bukkit.AZManager;
import fr.maxairfrance.azplugin.bukkit.EmauAPI;
import fr.maxairfrance.azplugin.bukkit.handlers.PLSPPlayerModel;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import pactify.client.api.plprotocol.metadata.PactifyScaleMetadata;
import pactify.client.api.plsp.packet.client.PLSPPacketEntityMeta;

import java.util.Optional;

public class AZSummon implements AZCommand {

    @Override
    public String name() {
        return "summon";
    }

    @Override
    public String permission() {
        return "emauapi.command.summon";
    }

    @Override
    public String description() {
        return "Invoque une entité avec une taille, un tag, et un niveau d'HP/force différent";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cCommande uniquement utilisable par un joueur.");
            return;
        }

        if (args.length < 3) {
            sender.sendMessage("§cUsage: /az summon <entity> <taille> [<x> <y> <z>]");
            return;
        }

        Player player = (Player) sender;
        Location location = parseLocation(args, player).orElse(null);
        if (location == null) {
            sender.sendMessage("§cErreur: La location est invalide");
            return;
        }

        float size = parseNumber(args[2], -1f);
        if (size == -1) {
            sender.sendMessage("§cErreur: La taille est invalide");
            return;
        }

        Optional<EntityType> optionalEntityType = getEntityType(args[1]);
        if (!optionalEntityType.isPresent()) {
            sender.sendMessage("§cErreur: Entité invalide");
            return;
        }

        Entity entity = location.getWorld().spawnEntity(location, optionalEntityType.get());
        PLSPPacketEntityMeta packetEntityMeta = new PLSPPacketEntityMeta(entity.getEntityId());
        packetEntityMeta.setScale(new PactifyScaleMetadata(size, size, size, size, size, true));

        EmauAPI.getInstance().entitiesSize.put(entity, packetEntityMeta);
        location.getWorld().getPlayers().forEach(p -> AZManager.sendPLSPMessage(p, packetEntityMeta));

        sender.sendMessage("§a[§2EmauSummon§a]§f Entité invoquée avec succès !");
    }

    private Optional<Location> parseLocation(String[] args, Player player) {
        if (args.length == 3) return Optional.of(player.getLocation());

        if (args.length >= 6) {
            try {
                return Optional.of(new Location(player.getWorld(),
                        Double.parseDouble(args[3]),
                        Double.parseDouble(args[4]),
                        Double.parseDouble(args[5])));
            } catch (NumberFormatException ignored) {
            }
        }
        return Optional.empty();
    }

    private <T extends Number> T parseNumber(String input, T defaultValue) {
        try {
            if (defaultValue instanceof Integer) {
                return (T) Integer.valueOf(input);
            } else if (defaultValue instanceof Float) {
                return (T) Float.valueOf(input);
            }
        } catch (NumberFormatException ignored) {
        }
        return defaultValue;
    }

    private Optional<EntityType> getEntityType(String entityName) {
        try {
            return Optional.ofNullable(EntityType.fromId(PLSPPlayerModel.valueOf(entityName).getId()));
        } catch (IllegalArgumentException | NullPointerException e) {
            return Optional.empty();
        }
    }
}
