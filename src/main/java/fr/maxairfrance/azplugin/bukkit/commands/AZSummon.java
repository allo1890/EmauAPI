package fr.maxairfrance.azplugin.bukkit.commands;

import fr.maxairfrance.azplugin.bukkit.AZManager;
import fr.maxairfrance.azplugin.bukkit.AZPlugin;
import fr.maxairfrance.azplugin.bukkit.handlers.PLSPPlayerModel;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pactify.client.api.plprotocol.metadata.PactifyTagMetadata;
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
        return "azplugin.command.summon";
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
            sender.sendMessage("§cUsage: /az summon <entity> <taille> [<x> <y> <z>] [<tag>] <niveau>");
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

        int level = (args.length > 6) ? parseNumber(args[args.length - 1], 1, 15) : 1;
        if (level == -1) {
            sender.sendMessage("§cLe niveau doit être compris entre 1 et 15 !");
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

        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            applyLevelStats(livingEntity, level);
            packetEntityMeta.setTag(generateTag(livingEntity, level));
        }

        AZPlugin.getInstance().entitiesSize.put(entity, packetEntityMeta);
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

    private int parseNumber(String input, int min, int max) {
        int value = parseNumber(input, -1);
        return (value >= min && value <= max) ? value : -1;
    }

    private Optional<EntityType> getEntityType(String entityName) {
        try {
            return Optional.ofNullable(EntityType.fromId(PLSPPlayerModel.valueOf(entityName).getId()));
        } catch (IllegalArgumentException | NullPointerException e) {
            return Optional.empty();
        }
    }

    private PactifyTagMetadata generateTag(LivingEntity entity, int level) {
        String typeTag = (entity instanceof Skeleton) ? "§aEsprit Cristallin"
                : (entity instanceof Zombie) ? "§aGuerrier"
                : "§aInvocation";

        String tagText = String.format("§cLv. %d %s §f%.1f\uEEEE♥", level, typeTag, entity.getHealth());
        PactifyTagMetadata tagMetadata = new PactifyTagMetadata();
        tagMetadata.setText(tagText);
        return tagMetadata;
    }

    private static void applyLevelStats(LivingEntity entity, int level) {
        double maxHealth = Math.min(20.0 + (150.0 * (level - 1)), 2048.0);
        entity.setMaxHealth(maxHealth);
        entity.setHealth(maxHealth);

        if (entity instanceof Skeleton) {
            equipSkeleton((Skeleton) entity, level);
        } else {
            entity.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, level - 1));
        }
    }

    public static Entity summonSkeletonAt(Location location, int level) {
        Skeleton skeleton = (Skeleton) location.getWorld().spawnEntity(location, EntityType.SKELETON);
        applyLevelStats(skeleton, level);

        String tag = "§cLv. " + level + " §aEsprit Cristallin §f" + (int) skeleton.getHealth() + "\uEEEE♥";
        PactifyTagMetadata tagMetadata = new PactifyTagMetadata();
        tagMetadata.setText(tag);

        PLSPPacketEntityMeta packetEntityMeta = new PLSPPacketEntityMeta(skeleton.getEntityId());
        packetEntityMeta.setTag(tagMetadata);

        AZPlugin.getInstance().entitiesSize.put(skeleton, packetEntityMeta);

        skeleton.setMetadata("summoned_by_lightning", new FixedMetadataValue(AZPlugin.getInstance(), true));

        for (Player player : location.getWorld().getPlayers()) {
            AZManager.sendPLSPMessage(player, packetEntityMeta);
        }
        return null;
    }
    public static Entity summonSilverFishAt(Location location, int level) {
        Silverfish silverfish = (Silverfish) location.getWorld().spawnEntity(location, EntityType.SILVERFISH);
        applyLevelStats(silverfish, level);

        String tag = "§cLv. " + level + " §aInvocation §f" + (int) silverfish.getHealth() + "\uEEEE♥";
        PactifyTagMetadata tagMetadata = new PactifyTagMetadata();
        tagMetadata.setText(tag);

        PLSPPacketEntityMeta packetEntityMeta = new PLSPPacketEntityMeta(silverfish.getEntityId());
        packetEntityMeta.setTag(tagMetadata);

        AZPlugin.getInstance().entitiesSize.put(silverfish, packetEntityMeta);

        silverfish.setMetadata("summoned_by_lightning", new FixedMetadataValue(AZPlugin.getInstance(), true));

        for (Player player : location.getWorld().getPlayers()) {
            AZManager.sendPLSPMessage(player, packetEntityMeta);
        }
        return null;
    }


    private static void equipSkeleton(Skeleton skeleton, int level) {
        ItemStack sword = new ItemStack(Material.IRON_SWORD);
        sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, Math.min(level * 2, 20));
        skeleton.getEquipment().setItemInMainHand(sword);
    }
}
