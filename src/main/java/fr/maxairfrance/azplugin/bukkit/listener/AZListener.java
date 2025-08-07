package fr.maxairfrance.azplugin.bukkit.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import fr.maxairfrance.azplugin.bukkit.AZManager;
import fr.maxairfrance.azplugin.bukkit.AZPlayer;
import fr.maxairfrance.azplugin.bukkit.EmauAPI;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
public class AZListener implements Listener {

    @EventHandler
    void onQuit(PlayerQuitEvent e){
        EmauAPI main = EmauAPI.getInstance();
        Player p = e.getPlayer();
        main.playersSeeChunks.remove(p);
    }

    @EventHandler
    void onDeath(EntityDeathEvent e) {
        EmauAPI.getInstance().entitiesSize.remove(e.getEntity());
    }

    public AZListener(EmauAPI main) {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(main, PacketType.Play.Server.SPAWN_ENTITY, PacketType.Play.Server.SPAWN_ENTITY_LIVING, PacketType.Play.Server.SPAWN_ENTITY_EXPERIENCE_ORB, PacketType.Play.Server.NAMED_ENTITY_SPAWN) {
            @Override
            public void onPacketSending(PacketEvent event) {
                int entityId = event.getPacket().getIntegers().read(0);
                Player player = event.getPlayer();
                Entity entity = event.getPacket().getEntityModifier(player.getWorld()).read(0);
                if (entity instanceof Player) {
                    AZPlayer azPlayer = EmauAPI.getAZManager().getPlayer((Player)entity);
                    AZManager.sendPLSPMessage(player, azPlayer.getPlayerMeta());
                } else if (EmauAPI.getInstance().entitiesSize.containsKey(entity)) {
                    AZManager.sendPLSPMessage(player, EmauAPI.getInstance().entitiesSize.get(entity));
                }
            }
        });
    }

}