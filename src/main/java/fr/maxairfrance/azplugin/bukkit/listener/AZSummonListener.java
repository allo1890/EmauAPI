package fr.maxairfrance.azplugin.bukkit.listener;

import fr.maxairfrance.azplugin.bukkit.AZManager;
import fr.maxairfrance.azplugin.bukkit.AZPlugin;
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
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.inventory.ItemStack;
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
        if (!AZPlugin.getInstance().entitiesSize.containsKey(entity)) return;

        if (entity.hasMetadata("summoned_by_lightning")) {
            return;
        }

        if (entity.hasMetadata("summoned")) {
            return;
        }

        if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event;

            if (damageEvent.getDamager() instanceof Player) {
                Player player = (Player) damageEvent.getDamager();
                damageMap.putIfAbsent(player.getUniqueId(), new HashMap<>());
                damageMap.get(player.getUniqueId()).put(entity, damageMap.get(player.getUniqueId()).getOrDefault(entity, 0.0) + event.getFinalDamage());
            }
        }

        double newHealth = Math.max(0, entity.getHealth() - event.getFinalDamage());
        int maxHealth = (int) entity.getMaxHealth();
        int level = getEntityLevel(entity);

        String newTag = "§cLv. " + level + " §f";
        if (entity instanceof Skeleton) {
            if (newHealth <= (double) maxHealth / 2) {
                spawnLightningAround(entity);
            }
            newTag += "§aEsprit Cristallin §f" + (int) newHealth + "\uEEEE♥";
        } else if (entity instanceof Zombie) {
            newTag += "§aGuerrier §f" + (int) newHealth + "\uEEEE♥";
        } else if (entity instanceof Silverfish) {
            newTag += "§aInvocation §f" + (int) newHealth + "\uEEEE♥";
        } else {
            newTag += "§7HP " + (int) newHealth + "§7/§c" + maxHealth + "§c HP";
        }

        PactifyTagMetadata tagMetadata = new PactifyTagMetadata();
        tagMetadata.setText(newTag);
        PLSPPacketEntityMeta packetEntityMeta = AZPlugin.getInstance().entitiesSize.get(entity);
        packetEntityMeta.setTag(tagMetadata);

        for (Player player : entity.getWorld().getPlayers()) {
            AZManager.sendPLSPMessage(player, packetEntityMeta);
            startTagUpdater();
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) return;

        LivingEntity entity = event.getEntity();
        if (!AZPlugin.getInstance().entitiesSize.containsKey(entity)) return;

        if (entity.hasMetadata("summoned_by_lightning")) {
            return;
        }

        List<Map.Entry<UUID, Map<Entity, Double>>> sortedEntries = new ArrayList<>(damageMap.entrySet());
        sortedEntries.sort((entry1, entry2) -> {
            double damage1 = entry1.getValue().getOrDefault(entity, 0.0);
            double damage2 = entry2.getValue().getOrDefault(entity, 0.0);
            return Double.compare(damage2, damage1);
        });

        List<String> topPlayers = new ArrayList<>();
        for (int i = 0; i < Math.min(10, sortedEntries.size()); i++) {
            Map.Entry<UUID, Map<Entity, Double>> entry = sortedEntries.get(i);
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player != null) {
                double damage = entry.getValue().getOrDefault(entity, 0.0);
                topPlayers.add(player.getName() + "§7 (" + (int) damage + " dégâts)");
            }
        }

        String message = "§fTop 10 des joueurs ayant infligé le plus de dégâts:";
        for (String topPlayer : topPlayers) {
            message += "\n§b" + topPlayer;
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(message);
        }

        Player bestPlayer = null;
        double maxDamage = 0.0;
        for (Map.Entry<UUID, Map<Entity, Double>> entry : damageMap.entrySet()) {
            Map<Entity, Double> playerDamageMap = entry.getValue();
            if (playerDamageMap.containsKey(entity)) {
                double damage = playerDamageMap.get(entity);
                if (damage > maxDamage) {
                    maxDamage = damage;
                    bestPlayer = Bukkit.getPlayer(entry.getKey());
                }
            }
        }

        if (bestPlayer != null) {
            int emeralds = random.nextInt(7) + 2;
            bestPlayer.getInventory().addItem(new ItemStack(Material.EMERALD, emeralds));
            bestPlayer.sendMessage("§fVous avez reçu §a" + emeralds + "§f émeraudes pour avoir infligé le plus de dégâts !");
        }
        damageMap.clear();
    }

    private void startTagUpdater() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Entity entity : AZPlugin.getInstance().entitiesSize.keySet()) {
                    if (entity instanceof LivingEntity) {
                        LivingEntity livingEntity = (LivingEntity) entity;

                        if (!AZPlugin.getInstance().entitiesSize.containsKey(entity)) continue;

                        int level = getEntityLevel(livingEntity);
                        String newTag = "§cLv. " + level + " §f";
                        double newHealth = livingEntity.getHealth();
                        int maxHealth = (int) livingEntity.getMaxHealth();

                        if (livingEntity instanceof Skeleton) {
                            newTag += "§aEsprit Cristallin §f" + (int) newHealth + "\uEEEE♥";
                        } else if (livingEntity instanceof Zombie) {
                            newTag += "§aGuerrier §f" + (int) newHealth + "\uEEEE♥";
                        } else if (livingEntity instanceof Silverfish) {
                            newTag += "§aInvocation §f" + (int) newHealth + "\uEEEE♥";
                        } else {
                            newTag += "§7HP " + (int) newHealth + "§7/§c" + maxHealth + "§c HP";
                        }

                        PactifyTagMetadata tagMetadata = new PactifyTagMetadata();
                        tagMetadata.setText(newTag);
                        PLSPPacketEntityMeta packetEntityMeta = AZPlugin.getInstance().entitiesSize.get(entity);
                        packetEntityMeta.setTag(tagMetadata);

                        for (Player player : livingEntity.getWorld().getPlayers()) {
                            AZManager.sendPLSPMessage(player, packetEntityMeta);
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(AZPlugin.getInstance(), 0L, 20L);
    }

    private void spawnLightningAround(Entity entity) {
        if (!(entity instanceof LivingEntity)) return;

        if (processedEntities.contains(entity)) return;

        World world = entity.getWorld();
        Location location = entity.getLocation();
        int radius = random.nextInt(7) + 2;

        processedEntities.add(entity);

        for (int i = 0; i < 3; i++) {
            double x = location.getX() + (random.nextDouble() * 2 - 1) * radius;
            double y = location.getY();
            double z = location.getZ() + (random.nextDouble() * 2 - 1) * radius;

            Location lightningLocation = new Location(world, x, y, z);
            world.strikeLightningEffect(lightningLocation);

            Entity skeleton = AZSummon.summonSkeletonAt(lightningLocation, 5);

            if (skeleton instanceof Skeleton) {
                skeleton.setMetadata("summoned_by_lightning", new FixedMetadataValue(AZPlugin.getInstance(), true));
            }
        }
    }

    private int getEntityLevel(LivingEntity entity) {
        String tag = AZPlugin.getInstance().entitiesSize.get(entity).getTag().getText();
        try {
            String[] parts = tag.split("§cLv. ");
            if (parts.length > 1) {
                return Integer.parseInt(parts[1].split(" ")[0]);
            }
        } catch (Exception e) {
            return 1;
        }
        return 1;
    }
}
