package fr.maxairfrance.azplugin.bukkit;

import fr.maxairfrance.azplugin.bukkit.commands.*;
import fr.maxairfrance.azplugin.bukkit.listener.AZListener;
import fr.maxairfrance.azplugin.bukkit.packets.PacketWindow;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pactify.client.api.plsp.packet.client.PLSPPacketEntityMeta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public final class EmauAPI extends JavaPlugin {

    @Getter static public EmauAPI instance;
    @Getter private static AZManager AZManager;
    public HashMap<Entity, PLSPPacketEntityMeta> entitiesSize;
    public List<Player> playersSeeChunks;
    private CommandManager commandManager;

    @Override
    public void onEnable() {
        instance = this;
        new Metrics(this, 21554);
        getServer().getPluginManager().registerEvents(new PacketWindow(this), this);
        AZManager = new AZManager(this);
        commandManager = new CommandManager();
        getCommand("az").setExecutor(commandManager);
        getCommand("az").setTabCompleter(new AZTabComplete());
        Bukkit.getPluginManager().registerEvents(new AZListener(this), this);
        entitiesSize = new HashMap<>();
        playersSeeChunks = new ArrayList<>();
        setCommands();
    }

    private void setCommands() {
        commandManager.addCommand(new AZList());
        commandManager.addCommand(new AZSize());
        commandManager.addCommand(new AZModel());
        commandManager.addCommand(new AZOpacity());
        commandManager.addCommand(new AZWorldEnv());
        commandManager.addCommand(new AZSummon());
        commandManager.addCommand(new AZItemRender());
    }

    @Override
    public void onDisable() {
        if (AZManager != null) {
            try {
                AZManager.close();
            } catch (IOException e) {
                getLogger().log(Level.WARNING, "Error closing AZManager", e);
            }
        }

        if (entitiesSize != null) {
            entitiesSize.clear();
        }
        if (playersSeeChunks != null) {
            playersSeeChunks.clear();
        }
    }
}
