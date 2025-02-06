package fr.mathip.azplugin.bukkit;

import fr.mathip.azplugin.bukkit.handlers.PopupType;
import fr.mathip.azplugin.bukkit.packets.PacketPopup;
import fr.mathip.azplugin.bukkit.utils.AZChatComponent;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Getter
public class PopupConfig {
    @Getter
    private static PopupConfig instance;
    private final File file;
    private final Main main;
    public List<PacketPopup> popups;

    public PopupConfig(Main main) {
        this.main = main;
        instance = this;
        file = new File(main.getDataFolder(), "popups.yml");
        if (!file.exists()) {
            main.saveResource("popups.yml", true);
        }
        load();
    }

    public void load() {
        popups = new ArrayList<>();
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        for (String popupPath : yamlConfiguration.getKeys(false)) {
            ConfigurationSection popupConf = yamlConfiguration.getConfigurationSection(popupPath);
            if (popupConf == null) continue;

            PacketPopup popup = new PacketPopup(popupPath, PopupType.valueOf(popupConf.getString("type", "DEFAULT")));
            AZChatComponent textComponent = new AZChatComponent("");
            for (String text : popupConf.getStringList("content")) {
                textComponent.getExtra().add(new AZChatComponent(text + "\n"));
            }
            popup.setTextComponent(textComponent);

            ConfigurationSection okButton = popupConf.getConfigurationSection("ok-button");
            if (okButton != null) {
                AZChatComponent okComponent = new AZChatComponent("");
                String command = okButton.getString("command", "");
                if (!command.isEmpty()) {
                    okComponent.setClickEvent(new AZChatComponent.ClickEvent("run_command", command));
                }
                popup.setOkComponent(okComponent);
            }

            ConfigurationSection cancelButton = popupConf.getConfigurationSection("cancel-button");
            if (cancelButton != null) {
                AZChatComponent cancelComponent = new AZChatComponent("");
                String command = cancelButton.getString("command", "");
                if (!command.isEmpty()) {
                    cancelComponent.setClickEvent(new AZChatComponent.ClickEvent("run_command", command));
                }
                popup.setCancelComponent(cancelComponent);
            }

            popup.setDefaultValue(popupConf.getString("default-value", ""));
            popup.setPassword(popupConf.getBoolean("is-password", false));

            popups.add(popup);
        }
    }

    public PacketPopup getPopupByName(String name) {
        return popups.stream()
                .filter(popup -> popup.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
