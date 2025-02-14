package fr.maxairfrance.azplugin.bukkit.listener;

import fr.maxairfrance.azplugin.bukkit.AZManager;
import fr.maxairfrance.azplugin.bukkit.AZPlugin;
import fr.maxairfrance.azplugin.bukkit.commands.AZSummon;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import pactify.client.api.plprotocol.metadata.PactifyTagMetadata;
import pactify.client.api.plsp.packet.client.PLSPPacketEntityMeta;

import java.util.Random;

public class AZSummonListener implements Listener {

    private final Random random = new Random();

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) return;

        LivingEntity entity = (LivingEntity) event.getEntity();
        if (!AZPlugin.getInstance().entitiesSize.containsKey(entity)) return;

        double newHealth = Math.max(0, entity.getHealth() - event.getFinalDamage());
        int maxHealth = (int) entity.getMaxHealth();
        int level = getEntityLevel(entity);

        String newTag = "§cLv. " + level + " §f";
        if (entity instanceof Skeleton) {
            if (newHealth <= maxHealth / 2) {
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
        }
    }

    private void spawnLightningAround(Entity entity) {
        if (!(entity instanceof LivingEntity)) return;
        World world = entity.getWorld();
        Location location = entity.getLocation();
        int radius = random.nextInt(7) + 2;

        for (int i = 0; i < 3; i++) {
            double x = location.getX() + (random.nextDouble() * 2 - 1) * radius;
            double y = location.getY();
            double z = location.getZ() + (random.nextDouble() * 2 - 1) * radius;

            Location lightningLocation = new Location(world, x, y, z);
            world.strikeLightningEffect(lightningLocation);

            AZSummon.summonSkeletonAt(lightningLocation, 5);
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
