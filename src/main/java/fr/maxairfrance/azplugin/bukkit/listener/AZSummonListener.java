package fr.maxairfrance.azplugin.bukkit.listener;

import fr.maxairfrance.azplugin.bukkit.AZManager;
import fr.maxairfrance.azplugin.bukkit.AZPlugin;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import pactify.client.api.plprotocol.metadata.PactifyTagMetadata;
import pactify.client.api.plsp.packet.client.PLSPPacketEntityMeta;

public class AZSummonListener implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) return;

        LivingEntity entity = (LivingEntity) event.getEntity();
        if (!AZPlugin.getInstance().entitiesSize.containsKey(entity)) return;

        double newHealth = Math.max(0, entity.getHealth() - event.getFinalDamage());
        int maxHealth = (int) entity.getMaxHealth();
        int level = getEntityLevel(entity);

        String newTag = "§bMobs lvl [§7" + level + "§b] - §c" + (int) newHealth + "§7/§c" + maxHealth + "§c HP";
        PactifyTagMetadata tagMetadata = new PactifyTagMetadata();
        tagMetadata.setText(newTag);

        PLSPPacketEntityMeta packetEntityMeta = AZPlugin.getInstance().entitiesSize.get(entity);
        packetEntityMeta.setTag(tagMetadata);

        for (Player player : entity.getWorld().getPlayers()) {
            AZManager.sendPLSPMessage(player, packetEntityMeta);
        }
    }

    private int getEntityLevel(LivingEntity entity) {
        String tag = AZPlugin.getInstance().entitiesSize.get(entity).getTag().getText();
        try {
            return Integer.parseInt(tag.split("\\[§7")[1].split("§b]")[0]);
        } catch (Exception e) {
            return 1;
        }
    }
}
