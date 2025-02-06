package fr.mathip.azplugin.bukkit;

import fr.mathip.azplugin.bukkit.handlers.PLSPConfFlag;
import fr.mathip.azplugin.bukkit.handlers.PLSPConfInt;
import fr.mathip.azplugin.bukkit.packets.PacketConf;
import fr.mathip.azplugin.bukkit.utils.*;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import pactify.client.api.plprotocol.model.cosmetic.PactifyCosmeticEquipment;
import pactify.client.api.plprotocol.model.cosmetic.PactifyCosmeticEquipmentSlot;
import pactify.client.api.plsp.packet.client.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class AZPlayer {
    private static final Pattern AZ_HOSTNAME_PATTERN;
    private final AZManager service;
    private final Player player;
    private final Set<Integer> scheduledTasks;
    private boolean joined;
    private int launcherProtocolVersion;

    private final PLSPPacketEntityMeta playerMeta;

    static {
        AZ_HOSTNAME_PATTERN = Pattern.compile("[\u0000\u0002]PAC([0-9A-F]{5})[\u0000\u0002]");
    }

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
        Bukkit.getScheduler().runTaskLater(Main. instance, () -> {
            this.sendCustomItems();
            AZManager.sendPLSPMessage(this.player, packetCosmeticEquipment);
        }, 40L);
    }

    public void free() {
        SchedulerUtil.cancelTasks(Main.getInstance(), this.scheduledTasks);
    }

    public void loadFlags() {
        ConfigManager config = ConfigManager.getInstance();
        PacketConf.setFlag(player, PLSPConfFlag.ATTACK_COOLDOWN, config.isAttackCooldown());
        PacketConf.setFlag(player, PLSPConfFlag.PLAYER_PUSH, config.isPlayerPush());
        PacketConf.setFlag(player, PLSPConfFlag.LARGE_HITBOX, config.isLargeHitBox());
        PacketConf.setFlag(player, PLSPConfFlag.SWORD_BLOCKING, config.isSwordBlocking());
        PacketConf.setFlag(player, PLSPConfFlag.HIT_AND_BLOCK, config.isHitAndBlock());
        PacketConf.setFlag(player, PLSPConfFlag.OLD_ENCHANTEMENTS, config.isOldEnchantments());
        PacketConf.setFlag(player, PLSPConfFlag.SIDEBAR_SCORES, config.isSidebarScore());
        PacketConf.setFlag(player, PLSPConfFlag.PVP_HIT_PRIORITY, config.isPvpHitPriority());
        PacketConf.setFlag(player, PLSPConfFlag.SEE_CHUNKS, config.isSeeChunks());
        PacketConf.setFlag(player, PLSPConfFlag.SMOOTH_EXPERIENCE_BAR, config.isSmoothExperienceBar());
        PacketConf.setFlag(player, PLSPConfFlag.SORT_TAB_LIST_BY_NAMES, config.isSortTabListByName());
        PacketConf.setFlag(player, PLSPConfFlag.SERVER_SIDE_ANVIL, config.isServerSideAnvil());
        PacketConf.setFlag(player, PLSPConfFlag.PISTONS_RETRACT_ENTITIES, config.isPistonRetractEntities());
        PacketConf.setFlag(player, PLSPConfFlag.HIT_INDICATOR, config.isHitIndicator());

        PacketConf.setInt(player, PLSPConfInt.CHAT_MESSAGE_MAX_SIZE, config.getChatMaxMessageSize());
        PacketConf.setInt(player, PLSPConfInt.MAX_BUILD_HEIGHT, config.getMaxBuildHeight());

        for (PacketUiComponent uiComponent : config.getUIComponents()) {
            AZChatComponent azChatComponent = new AZChatComponent(uiComponent.getText());
            if (!uiComponent.getHoverText().equals("")) {
                azChatComponent.setHoverEvent(new AZChatComponent.HoverEvent("show_text", uiComponent.getHoverText().replaceAll("%player%", player.getName())));
            }
            if (!uiComponent.getCommmand().equals("")) {
                azChatComponent.setClickEvent(new AZChatComponent.ClickEvent("run_command", uiComponent.getCommmand().replaceAll("%player%", player.getName())));
            }
            PLSPPacketUiComponent packetUiComponent = new PLSPPacketUiComponent(uiComponent.getName(), azChatComponent);
            AZManager.sendPLSPMessage(player, packetUiComponent);
        }
    }

    public boolean hasLauncher() {
        return this.launcherProtocolVersion > 0;
    }

    public AZPlayer(final AZManager service, final Player player) {
        this.scheduledTasks = new HashSet<Integer>();
        this.service = service;
        this.player = player;
        this.playerMeta = new PLSPPacketEntityMeta(player.getEntityId());
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> loadFlags());
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

    @Override
    public int hashCode() {
        int result = 1;
        final Object $service = this.getService();
        result = result * 59 + (($service == null) ? 43 : $service.hashCode());
        final Object $player = this.getPlayer();
        result = result * 59 + (($player == null) ? 43 : $player.hashCode());
        final Object $scheduledTasks = this.getScheduledTasks();
        result = result * 59 + (($scheduledTasks == null) ? 43 : $scheduledTasks.hashCode());
        result = result * 59 + (this.isJoined() ? 79 : 97);
        result = result * 59 + this.getLauncherProtocolVersion();
        return result;
    }

    @Override
    public String toString() {
        return "AZPlayer(service=" + this.getService() + ", player=" + this.getPlayer() + ", scheduledTasks=" + this.getScheduledTasks() + ", joined=" + this.isJoined() + ", launcherProtocolVersion=" + this.getLauncherProtocolVersion() + ")";
    }

    public static boolean hasAZLauncher(final Player player) {
        return Main.getAZManager().getPlayer(player).hasLauncher();
    }

    private void sendCustomItems() {
        short[] additionalContents = {
                768, 769, 770, 771, 772, 3072, 3076, 3079, 773, 774, 775, 776 };
        PLSPPacketAdditionalContent additionalContent = new PLSPPacketAdditionalContent(additionalContents);
        AZManager.sendPLSPMessage(this.player, additionalContent);
    }

    public void updateMeta() {
        AZManager.sendPLSPMessage(player, this.playerMeta);
        for (Player pl : this.player.getWorld().getPlayers()) {
            AZManager.sendPLSPMessage(pl, this.playerMeta);
        }
    }
}