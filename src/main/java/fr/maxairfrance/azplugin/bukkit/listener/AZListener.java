package fr.maxairfrance.azplugin.bukkit.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import fr.maxairfrance.azplugin.bukkit.AZManager;
import fr.maxairfrance.azplugin.bukkit.AZPlayer;
import fr.maxairfrance.azplugin.bukkit.EmauAPI;
import fr.maxairfrance.azplugin.bukkit.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static fr.maxairfrance.azplugin.bukkit.AZPlayer.hasAZLauncher;

public class AZListener implements Listener {

    @EventHandler
    void onQuit(PlayerQuitEvent e){
        EmauAPI main = EmauAPI.getInstance();
        Player p = e.getPlayer();
        main.playersSeeChunks.remove(p);
    }

    @EventHandler
    void onJoint(PlayerJoinEvent e){
        Player player = e.getPlayer();
        EmauAPI main = EmauAPI.getInstance();
        ConfigManager config = ConfigManager.getInstance();

        if (hasAZLauncher(player)){
            if (config.getJoinWithAZCommands() != null) {
                config.getJoinWithAZCommands().forEach(command -> {
                    Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), command.replaceAll("%player%", player.getName()));
                });
            }

        } else {
            if (config.getJoinWithoutAZCommands() != null) {
                config.getJoinWithoutAZCommands().forEach(command -> {
                    Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), command.replaceAll("%player%", player.getName()));
                });
            }
        }

        if (main.isUpdate && config.isUpdateMessage() && player.hasPermission("azplugin.update")) {
            player.sendMessage("§6Une nouvelle version du §bAZPlugin§6 a été détecté !");
            player.sendMessage("§bhttps://www.spigotmc.org/resources/azplugin.115548/");
        }
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