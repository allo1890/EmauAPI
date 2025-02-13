package fr.maxairfrance.azplugin.bukkit.commands;

import fr.maxairfrance.azplugin.bukkit.AZManager;
import fr.maxairfrance.azplugin.bukkit.AZPlugin;
import fr.maxairfrance.azplugin.bukkit.handlers.PLSPPlayerModel;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pactify.client.api.plprotocol.metadata.PactifyTagMetadata;
import pactify.client.api.plprotocol.metadata.PactifyScaleMetadata;
import pactify.client.api.plsp.packet.client.PLSPPacketEntityMeta;

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
        float size;
        Location location;
        Player player = (Player) sender;

        if (args.length < 3) {
            sender.sendMessage("§cUsage: /az summon <entity> <taille> [<x> <y> <z>] [<tag>] <niveau>");
            return;
        }

        if (args.length == 3) {
            location = player != null ? player.getLocation() : null;
        } else if (args.length >= 6) {
            try {
                float x = Float.parseFloat(args[3]);
                float y = Float.parseFloat(args[4]);
                float z = Float.parseFloat(args[5]);
                World world = player != null ? player.getWorld() : Bukkit.getWorld("world");
                location = new Location(world, x, y, z);
            } catch (NumberFormatException e) {
                sender.sendMessage("§cErreur: La location est invalide");
                return;
            }
        } else {
            sender.sendMessage("§cUsage: /az summon <entity> <taille> [<x> <y> <z>] [<tag>] <niveau>");
            return;
        }

        try {
            size = Float.parseFloat(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cErreur: La taille est invalide");
            return;
        }

        int level = 1;
        try {
            if (args.length > 6) {
                level = Integer.parseInt(args[args.length - 1]);
                if (level < 1 || level > 20) {
                    sender.sendMessage("§cLe niveau doit être compris entre 1 et 20 !");
                    return;
                }
            }
        } catch (NumberFormatException e) {
            sender.sendMessage("§cErreur: Le niveau est invalide");
            return;
        }

        Entity entity = location.getWorld().spawnEntity(location, EntityType.fromId(PLSPPlayerModel.valueOf(args[1]).getId()));

        PLSPPacketEntityMeta packetEntityMeta = new PLSPPacketEntityMeta(entity.getEntityId());
        packetEntityMeta.setScale(new PactifyScaleMetadata(size, size, size, size, size, true));

        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            applyLevelStats(livingEntity, level);

            String tag = "§bMobs lvl [§7" + level + "§b] - §c" + (int) livingEntity.getHealth() + "§7/§c" + (int) livingEntity.getMaxHealth() + "§c HP";

            PactifyTagMetadata tagMetadata = new PactifyTagMetadata();
            tagMetadata.setText(tag);
            packetEntityMeta.setTag(tagMetadata);
        }

        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            applyLevelStats(livingEntity, level);
        }

        AZPlugin.getInstance().entitiesSize.put(entity, packetEntityMeta);
        for (Player player1 : location.getWorld().getPlayers()) {
            AZManager.sendPLSPMessage(player1, packetEntityMeta);
        }

        sender.sendMessage("§a[§2EmauSummon§a]§f Entité crée avec succès avec taille, tag et niveau !");
    }

    private void applyLevelStats(LivingEntity entity, int level) {
        double baseHealth = 20.0;
        double additionalHealth = 150.0 * (level - 1);
        double health = baseHealth + additionalHealth;

        health = Math.min(health, 2048.0);

        entity.setMaxHealth(health);
        entity.setHealth(health);

        if (entity.getType() == EntityType.SKELETON) {
            Skeleton skeleton = (Skeleton) entity;
            ItemStack bow = new ItemStack(Material.BOW);
            bow.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, Math.min(level * 2, 20));
            skeleton.getEquipment().setItemInMainHand(bow);
        } else {
            int baseAttack = 5;
            int additionalAttack = level;
            int attackDamage = baseAttack + additionalAttack;
            entity.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, attackDamage - 1));
        }
    }
}

