package fr.mathip.azplugin.bukkit.config;

import fr.mathip.azplugin.bukkit.AZPlugin;
import fr.mathip.azplugin.bukkit.packets.PacketUiComponent;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;

@Getter
@Setter
@Slf4j
public class ConfigManager {
    private final AZPlugin main;
    @Getter
    private static ConfigManager instance;
    private boolean attackCooldown;
    private boolean playerPush;
    private boolean largeHitBox;
    private boolean swordBlocking;
    private boolean hitAndBlock;
    private boolean oldEnchantments;
    private boolean pvpHitPriority;
    private boolean seeChunks;
    private boolean sidebarScore;
    private boolean smoothExperienceBar;
    private boolean sortTabListByName;
    private boolean serverSideAnvil;
    private boolean pistonRetractEntities;
    private boolean hitIndicator;
    private boolean updateMessage;
    private int chatMaxMessageSize;
    private int maxBuildHeight;
    private ArrayList<String> joinWithAZCommands = new ArrayList<>();
    private ArrayList<String> joinWithoutAZCommands = new ArrayList<>();
    private ArrayList<String> specialInventoryCharacters = new ArrayList<>();
    private ArrayList<PacketUiComponent> UIComponents = new ArrayList<>();

    public ConfigManager(AZPlugin main) {
        this.main = main;
        instance = this;
        initConfig();
    }

    public void initConfig() {
        joinWithAZCommands = (ArrayList<String>) main.getConfig().get("join-with-az-commands");
        joinWithoutAZCommands = (ArrayList<String>) main.getConfig().get("join-without-az-commands");

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

        specialInventoryCharacters = (ArrayList<String>) main.getConfig().get("special-transparent-inventory-character");
        new PopupConfig(AZPlugin.getInstance());

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
        log.info("Config loaded !");
    }
}
