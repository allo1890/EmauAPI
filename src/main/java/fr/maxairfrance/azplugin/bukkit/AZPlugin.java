package fr.maxairfrance.azplugin.bukkit;

import fr.maxairfrance.azplugin.bukkit.commands.*;
import fr.maxairfrance.azplugin.bukkit.config.ConfigManager;
import fr.maxairfrance.azplugin.bukkit.listener.AZListener;
import fr.maxairfrance.azplugin.bukkit.packets.PacketWindow;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import pactify.client.api.plsp.packet.client.PLSPPacketEntityMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class AZPlugin extends JavaPlugin {

    @Getter
    static public AZPlugin instance;
    @Getter
    private static AZManager AZManager;
    public HashMap<Entity, PLSPPacketEntityMeta> entitiesSize;
    public List<Player> playersSeeChunks;
    private BukkitTask bukkitTask;
    public boolean isUpdate;
    private CommandManager commandManager;
    private ConfigManager configManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        Metrics metrics = new Metrics(this, 21554);
        new ConfigManager(this);
        getServer().getPluginManager().registerEvents(new PacketWindow(this), this);
        AZManager = new AZManager(this);
        commandManager = new CommandManager();
        getCommand("az").setExecutor(commandManager);
        getCommand("az").setTabCompleter(new AZTabComplete());
        Bukkit.getPluginManager().registerEvents(new AZListener(this), this);
        entitiesSize = new HashMap<>();
        playersSeeChunks = new ArrayList<>();
        setCommands();
        isUpdate = new AZUpdate(this, 115548).checkForUpdate();
        if (isUpdate) {
            getLogger().info("Une nouvelle version du plugin a été détecté !");
            getLogger().info("Il est recommendé de le mettre à jour ici: https://www.spigotmc.org/resources/azplugin.115548/");
        }
    }

    private void setCommands() {
        commandManager.addCommand(new AZList());
        commandManager.addCommand(new AZSize());
        commandManager.addCommand(new AZModel());
        commandManager.addCommand(new AZOpacity());
        commandManager.addCommand(new AZWorldEnv());
        commandManager.addCommand(new AZVignette());
        commandManager.addCommand(new AZSeechunks());
        commandManager.addCommand(new AZTag());
        commandManager.addCommand(new AZSubTag());
        commandManager.addCommand(new AZSupTag());
        commandManager.addCommand(new AZSummon());
        commandManager.addCommand(new AZItemRender());
        commandManager.addCommand(new AZPopup());
    }

    public String getPluginVersion() {
        return this.getDescription().getVersion();
    }

    @Override
    public void onDisable() {

    }
}
