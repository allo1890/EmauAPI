package fr.maxairfrance.azplugin.bukkit.listener;

import fr.maxairfrance.azplugin.bukkit.AZManager;
import fr.maxairfrance.azplugin.bukkit.EmauAPI;
import fr.maxairfrance.azplugin.bukkit.commands.AZSummon;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import pactify.client.api.plprotocol.metadata.PactifyTagMetadata;
import pactify.client.api.plsp.packet.client.PLSPPacketEntityMeta;

import java.util.*;

public class AZSummonListener implements Listener {

    private final Random random = new Random();
    private final Set<Entity> processedEntities = new HashSet<>();
    private final Map<UUID, Map<Entity, Double>> damageMap = new HashMap<>();

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) return;

        LivingEntity entity = (LivingEntity) event.getEntity();
        if (!EmauAPI.getInstance().entitiesSize.containsKey(entity) || entity.hasMetadata("summoned_by_lightning") || entity.hasMetadata("summoned")) {
            return;
        }

        if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event;
            if (damageEvent.getDamager() instanceof Player) {
                recordPlayerDamage((Player) damageEvent.getDamager(), entity, event.getFinalDamage());
            }
        }

        updateEntityTag(entity, entity.getHealth() - event.getFinalDamage());

        if (entity instanceof Skeleton && entity.getHealth() <= entity.getMaxHealth() / 2) {
            spawnLightningAround(entity);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (!EmauAPI.getInstance().entitiesSize.containsKey(entity) || entity.hasMetadata("summoned_by_lightning")) {
            return;
        }

        List<Map.Entry<UUID, Double>> sortedEntries = getSortedPlayerDamage(entity);
        announceTopDamage(sortedEntries);
        rewardBestPlayer(sortedEntries, entity);
        damageMap.clear();
    }

    private void recordPlayerDamage(Player player, LivingEntity entity, double damage) {
        damageMap.putIfAbsent(player.getUniqueId(), new HashMap<>());
        damageMap.get(player.getUniqueId()).merge(entity, damage, Double::sum);
    }

    private List<Map.Entry<UUID, Double>> getSortedPlayerDamage(LivingEntity entity) {
        List<Map.Entry<UUID, Double>> sortedEntries = new ArrayList<>();

        for (Map.Entry<UUID, Map<Entity, Double>> entry : damageMap.entrySet()) {
            double damage = entry.getValue().getOrDefault(entity, 0.0);
            if (damage > 0) sortedEntries.add(new AbstractMap.SimpleEntry<>(entry.getKey(), damage));
        }

        sortedEntries.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        return sortedEntries;
    }

    private void announceTopDamage(List<Map.Entry<UUID, Double>> sortedEntries) {
        StringBuilder message = new StringBuilder("§fTop 10 des joueurs ayant infligé le plus de dégâts:");
        for (int i = 0; i < Math.min(10, sortedEntries.size()); i++) {
            Player player = Bukkit.getPlayer(sortedEntries.get(i).getKey());
            if (player != null) {
                message.append("\n§b").append(player.getName()).append("§7 (").append(sortedEntries.get(i).getValue().intValue()).append(" dégâts)");
            }
        }
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(message.toString()));
    }

    private void rewardBestPlayer(List<Map.Entry<UUID, Double>> sortedEntries, LivingEntity entity) {
        if (!sortedEntries.isEmpty()) {
            Player bestPlayer = Bukkit.getPlayer(sortedEntries.get(0).getKey());
            if (bestPlayer != null) {
                int emeralds = random.nextInt(7) + 2;
                bestPlayer.getInventory().addItem(new ItemStack(Material.EMERALD, emeralds));
                bestPlayer.sendMessage("§fVous avez reçu §a" + emeralds + "§f émeraudes pour avoir infligé le plus de dégâts !");
            }
        }
    }

    private void updateEntityTag(LivingEntity entity, double newHealth) {
        newHealth = Math.max(0, newHealth);
        int maxHealth = (int) entity.getMaxHealth();
        int level = getEntityLevel(entity);

        String newTag = "§cLv. " + level + " §f" + getEntityDisplayName(entity, (int) newHealth, maxHealth);
        PactifyTagMetadata tagMetadata = new PactifyTagMetadata();
        tagMetadata.setText(newTag);

        PLSPPacketEntityMeta packetEntityMeta = EmauAPI.getInstance().entitiesSize.get(entity);
        packetEntityMeta.setTag(tagMetadata);

        entity.getWorld().getPlayers().forEach(player -> AZManager.sendPLSPMessage(player, packetEntityMeta));
    }

    private String getEntityDisplayName(LivingEntity entity, int newHealth, int maxHealth) {
        if (entity instanceof Skeleton) return "§aEsprit Cristallin §f" + newHealth + "\uEEEE♥";
        if (entity instanceof Zombie) return "§aGuerrier §f" + newHealth + "\uEEEE♥";
        if (entity instanceof Silverfish) return "§aInvocation §f" + newHealth + "\uEEEE♥";
        return "§7HP " + newHealth + "§7/§c" + maxHealth + "§c HP";
    }

    private int getEntityLevel(LivingEntity entity) {
        String tag = EmauAPI.getInstance().entitiesSize.get(entity).getTag().getText();
        try {
            return Integer.parseInt(tag.split("§cLv. ")[1].split(" ")[0]);
        } catch (Exception e) {
            return 1;
        }
    }

    private void spawnLightningAround(Entity entity) {
        if (!(entity instanceof LivingEntity) || processedEntities.contains(entity)) return;

        World world = entity.getWorld();
        Location location = entity.getLocation();
        int radius = random.nextInt(7) + 2;
        processedEntities.add(entity);

        for (int i = 0; i < 3; i++) {
            Location lightningLocation = location.clone().add((random.nextDouble() * 2 - 1) * radius, 0, (random.nextDouble() * 2 - 1) * radius);
            world.strikeLightningEffect(lightningLocation);

            summonAndMarkEntity(AZSummon.summonSkeletonAt(lightningLocation, 5), "summoned_by_lightning");
            summonAndMarkEntity(AZSummon.summonSilverFishAt(lightningLocation, 1), "summoned_by_lightning");
        }
    }

    private void summonAndMarkEntity(Entity entity, String metadata) {
        if (entity != null) {
            entity.setMetadata(metadata, new FixedMetadataValue(EmauAPI.getInstance(), true));
        }
    }
}
