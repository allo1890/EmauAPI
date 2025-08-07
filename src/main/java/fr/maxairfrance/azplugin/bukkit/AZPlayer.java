package fr.maxairfrance.azplugin.bukkit;

import fr.maxairfrance.azplugin.bukkit.handlers.PLSPFlag;
import fr.maxairfrance.azplugin.bukkit.handlers.PLSPConfInt;
import fr.maxairfrance.azplugin.bukkit.packets.PacketFlag;
import fr.maxairfrance.azplugin.bukkit.packets.PacketUiComponent;
import fr.maxairfrance.azplugin.bukkit.utils.AZChatComponent;
import fr.maxairfrance.azplugin.bukkit.utils.AZItemStack;
import fr.maxairfrance.azplugin.bukkit.utils.BukkitUtil;
import fr.maxairfrance.azplugin.bukkit.utils.SchedulerUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import pactify.client.api.plprotocol.metadata.PactifyModelMetadata;
import pactify.client.api.plprotocol.model.cosmetic.PactifyCosmeticEquipment;
import pactify.client.api.plprotocol.model.cosmetic.PactifyCosmeticEquipmentSlot;
import pactify.client.api.plsp.packet.client.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class AZPlayer {

    private static final Pattern AZ_HOSTNAME_PATTERN = Pattern.compile("[\u0000\u0002]PAC([0-9A-F]{5})[\u0000\u0002]");
    private final AZManager service;
    private final Player player;
    private final Set<Integer> scheduledTasks = new HashSet<>();
    private boolean joined;
    private int launcherProtocolVersion;
    private final PLSPPacketPlayerMeta playerMeta;
    private final PLSPPacketEntityMeta entityMeta;

    public void init() {
        final List<MetadataValue> hostnameMeta = this.player.getMetadata("AZPlugin:hostname");
        if (!hostnameMeta.isEmpty()) {
            final String hostname = hostnameMeta.get(0).asString();
            final Matcher m = AZPlayer.AZ_HOSTNAME_PATTERN.matcher(hostname);
            if (m.find()) {
                this.launcherProtocolVersion = Math.max(1, Integer.parseInt(m.group(1), 16));
            }
        }
        else {
            this.service.getPlugin().getLogger().warning("Unable to verify the launcher of " + this.player.getName() + ": it probably logged when the plugin was disabled!");
        }
        BukkitUtil.addChannel(this.player, "PLSP");
    }

    public void join() {
        this.joined = true;
        AZManager.sendPLSPMessage(this.player, new PLSPPacketReset());

            AZItemStack azItemStack = new AZItemStack(new ItemStack(Material.DIRT));
            PactifyCosmeticEquipment cosmeticEquipment = new PactifyCosmeticEquipment(azItemStack);
            AZChatComponent prefixText = new AZChatComponent("§bKit ");
            prefixText.setClickEvent(new AZChatComponent.ClickEvent("run_command", "/kit pvp"));
            prefixText.setHoverEvent(new AZChatComponent.HoverEvent("show_text", "§béquiper le kit pvp"));
            cosmeticEquipment.setTooltipPrefix(prefixText);

            PLSPPacketPlayerCosmeticEquipment packetCosmeticEquipment = new PLSPPacketPlayerCosmeticEquipment();
            packetCosmeticEquipment.setPlayerId(this.player.getUniqueId());
            packetCosmeticEquipment.setSlot(PactifyCosmeticEquipmentSlot.CUSTOM_1);
            packetCosmeticEquipment.setEquipment(cosmeticEquipment);

            Bukkit.getScheduler().runTask(EmauAPI.instance, () -> {
                AZManager.sendPLSPMessage(this.player, packetCosmeticEquipment);
            });


        this.sendCustomItems();
    }

    public void free() {
        SchedulerUtil.cancelTasks(EmauAPI.getInstance(), this.scheduledTasks);
    }

    public void loadFlags() {
        PacketFlag.setFlag(player, PLSPFlag.ATTACK_COOLDOWN, false);
        PacketFlag.setFlag(player, PLSPFlag.PLAYER_PUSH, false);
        PacketFlag.setFlag(player, PLSPFlag.LARGE_HITBOX, true);
        PacketFlag.setFlag(player, PLSPFlag.SWORD_BLOCKING, true);
        PacketFlag.setFlag(player, PLSPFlag.HIT_AND_BLOCK, true);
        PacketFlag.setFlag(player, PLSPFlag.OLD_ENCHANTEMENTS, true);
        PacketFlag.setFlag(player, PLSPFlag.SIDEBAR_SCORES, false);
        PacketFlag.setFlag(player, PLSPFlag.PVP_HIT_PRIORITY, true);
        PacketFlag.setFlag(player, PLSPFlag.SEE_CHUNKS, false);
        PacketFlag.setFlag(player, PLSPFlag.SMOOTH_EXPERIENCE_BAR, true);
        PacketFlag.setFlag(player, PLSPFlag.SORT_TAB_LIST_BY_NAMES, false);
        PacketFlag.setFlag(player, PLSPFlag.SERVER_SIDE_ANVIL, false);
        PacketFlag.setFlag(player, PLSPFlag.PISTONS_RETRACT_ENTITIES, false);
        PacketFlag.setFlag(player, PLSPFlag.HIT_INDICATOR, false);

        PacketFlag.setInt(player, PLSPConfInt.CHAT_MESSAGE_MAX_SIZE, 150);
        PacketFlag.setInt(player, PLSPConfInt.MAX_BUILD_HEIGHT, 160);

        List<PacketUiComponent> uiComponents = Arrays.asList(
                new PacketUiComponent("Menu Emauzium", "gamemenu_achievements", "", "/emauzium"),
                new PacketUiComponent("Acheter Points VIP", "gamemenu_statistics", "", "/www"),
                new PacketUiComponent("", "playerinv_cosmetic", "", "/backpack"),
                new PacketUiComponent("§2Faction", "playerinv_btn1", "\uEEEE➡ §9/f", "/f"),
                new PacketUiComponent("§2Banques", "playerinv_btn2", "\uEEEE➡ §9/bank", "/bank"),
                new PacketUiComponent("§2Boutique", "playerinv_btn3", "\uEEEE➡ §9/shop", "/shop"),
                new PacketUiComponent("§aAller au Spawn", "playerinv_btn6", "\uEEEE➡ §9/spawn", "/spawn"),
                new PacketUiComponent("§aRetour au HUB", "playerinv_btn7", "\uEEEE➡ §9/hub", "/hub")
        );

        for (PacketUiComponent uiComponent : uiComponents) {
            AZChatComponent azChatComponent = new AZChatComponent(uiComponent.getText());

            if (!uiComponent.getHoverText().isEmpty()) {
                azChatComponent.setHoverEvent(
                        new AZChatComponent.HoverEvent("show_text", uiComponent.getHoverText().replace("%player%", player.getName()))
                );
            }

            if (!uiComponent.getCommmand().isEmpty()) {
                azChatComponent.setClickEvent(
                        new AZChatComponent.ClickEvent("run_command", uiComponent.getCommmand().replace("%player%", player.getName()))
                );
            }

            PLSPPacketUiComponent packetUiComponent = new PLSPPacketUiComponent(uiComponent.getName(), azChatComponent);
            AZManager.sendPLSPMessage(player, packetUiComponent);
        }
    }

    public boolean hasLauncher() {
        return this.launcherProtocolVersion == 16;
    }

    public AZPlayer(AZManager service, Player player) {
        this.service = service;
        this.player = player;
        this.playerMeta = new PLSPPacketPlayerMeta(player.getUniqueId());
        this.entityMeta = new PLSPPacketEntityMeta(player.getEntityId());
        this.playerMeta.setModel(new PactifyModelMetadata(-1));
        Bukkit.getScheduler().runTaskAsynchronously(EmauAPI.getInstance(), this::loadFlags);
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AZPlayer)) {
            return false;
        }
        final AZPlayer other = (AZPlayer)o;
        if (!other.canEqual(this)) {
            return false;
        }
        final Object this$service = this.getService();
        final Object other$service = other.getService();
        Label_0065: {
            if (this$service == null) {
                if (other$service == null) {
                    break Label_0065;
                }
            }
            else if (this$service.equals(other$service)) {
                break Label_0065;
            }
            return false;
        }
        final Object this$player = this.getPlayer();
        final Object other$player = other.getPlayer();
        Label_0102: {
            if (this$player == null) {
                if (other$player == null) {
                    break Label_0102;
                }
            }
            else if (this$player.equals(other$player)) {
                break Label_0102;
            }
            return false;
        }
        final Object this$scheduledTasks = this.getScheduledTasks();
        final Object other$scheduledTasks = other.getScheduledTasks();
        if (this$scheduledTasks == null) {
            if (other$scheduledTasks == null) {
                return this.isJoined() == other.isJoined() && this.getLauncherProtocolVersion() == other.getLauncherProtocolVersion();
            }
        }
        else if (this$scheduledTasks.equals(other$scheduledTasks)) {
            return this.isJoined() == other.isJoined() && this.getLauncherProtocolVersion() == other.getLauncherProtocolVersion();
        }
        return false;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof AZPlayer;
    }

    public int hashCode() {
        int result = 1;
        Object $service = this.getService();
        result = result * 59 + ($service == null ? 43 : $service.hashCode());
        Object $player = this.getPlayer();
        result = result * 59 + ($player == null ? 43 : $player.hashCode());
        Object $scheduledTasks = this.getScheduledTasks();
        result = result * 59 + ($scheduledTasks == null ? 43 : $scheduledTasks.hashCode());
        result = result * 59 + (this.isJoined() ? 79 : 97);
        result = result * 59 + this.getLauncherProtocolVersion();
        return result;
    }

    public String toString() {
        return "AZClientPlayer(service=" + this.getService() + ", player=" + this.getPlayer() + ", scheduledTasks=" + this.getScheduledTasks() + ", joined=" + this.isJoined() + ", launcherProtocolVersion=" + this.getLauncherProtocolVersion() + ")";
    }

    public static boolean hasAZLauncher(Player player) {
        AZManager azManager = EmauAPI.getAZManager();
        if (azManager == null) {
            Bukkit.getLogger().warning("AZManager is null.");
            return false;
        }

        AZPlayer azPlayer = azManager.getPlayer(player);
        if (azPlayer == null) {
            Bukkit.getLogger().warning("AZPlayer is null for player: " + player.getName());
            return false;
        }

        return azPlayer.hasLauncher();
    }


    public void updateMeta() {
        for (Player pl : this.player.getWorld().getPlayers()) {
            AZManager.sendPLSPMessage(pl, this.entityMeta);
        }

        AZManager.sendPLSPMessage(this.player, this.playerMeta);
    }

    private void sendCustomItems() {
        short[] additionalContents = new short[]{768, 769, 770, 771, 772, 3072, 3076, 3079, 773, 774, 775, 776};
        PLSPPacketAdditionalContent additionalContent = new PLSPPacketAdditionalContent(additionalContents);
        AZManager.sendPLSPMessage(this.player, additionalContent);
    }
}