package fr.mathip.azplugin.bukkit;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.bukkit.configuration.ConfigurationSection;
import java.util.ArrayList;
import java.util.List;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConfigManager {
    @Getter
    static ConfigManager instance;
    final Main main;

    boolean attackCooldown, playerPush, largeHitBox, swordBlocking, hitAndBlock, oldEnchantments, pvpHitPriority,
            seeChunks, sidebarScore, smoothExperienceBar, sortTabListByName, serverSideAnvil, pistonRetractEntities, hitIndicator, updateMessage;

    int chatMaxMessageSize, maxBuildHeight;

    List<String> joinWithAZCommands = new ArrayList<>();
    List<String> joinWithoutAZCommands = new ArrayList<>();
    List<String> specialInventoryCharacters = new ArrayList<>();
    List<PacketUiComponent> UIComponents = new ArrayList<>();

    public ConfigManager(Main main) {
        this.main = main;
        instance = this;
        initConfig();
    }

    public void initConfig() {
        joinWithAZCommands = main.getConfig().getStringList("join-with-az-commands");
        joinWithoutAZCommands = main.getConfig().getStringList("join-without-az-commands");
        specialInventoryCharacters = main.getConfig().getStringList("special-transparent-inventory-character");

        updateMessage = main.getConfig().getBoolean("update-message");
        attackCooldown = main.getConfig().getBoolean("attack_cooldown");
        playerPush = main.getConfig().getBoolean("player_push");
        largeHitBox = main.getConfig().getBoolean("large_hitbox");
        swordBlocking = main.getConfig().getBoolean("sword_blocking");
        hitAndBlock = main.getConfig().getBoolean("hit_and_block");
        oldEnchantments = main.getConfig().getBoolean("old_enchantments");
        pvpHitPriority = main.getConfig().getBoolean("pvp_hit_priority");
        seeChunks = main.getConfig().getBoolean("see_chunks");
        sidebarScore = main.getConfig().getBoolean("sidebar_scores");
        smoothExperienceBar = main.getConfig().getBoolean("smooth_experience_bar");
        sortTabListByName = main.getConfig().getBoolean("sort_tab_list_by_names");
        serverSideAnvil = main.getConfig().getBoolean("server_side_anvil");
        pistonRetractEntities = main.getConfig().getBoolean("pistons_retract_entities");
        hitIndicator = main.getConfig().getBoolean("hit_indicator");

        chatMaxMessageSize = main.getConfig().getInt("chat_message_max_size");
        maxBuildHeight = main.getConfig().getInt("max_build_height");

        new PopupConfig(Main.getInstance());

        UIComponents = new ArrayList<>();
        ConfigurationSection cs = main.getConfig().getConfigurationSection("ui-buttons");
        if (cs != null) {
            for (String pathName : cs.getKeys(false)) {
                if (cs.getBoolean(pathName + ".enable")) {
                    PacketUiComponent uiComponent = new PacketUiComponent(
                            cs.getString(pathName + ".text"),
                            pathName,
                            cs.getString(pathName + ".hover-text"),
                            cs.getString(pathName + ".command")
                    );
                    UIComponents.add(uiComponent);
                }
            }
        }

        main.getLogger().info("Config loaded !");
    }

}
