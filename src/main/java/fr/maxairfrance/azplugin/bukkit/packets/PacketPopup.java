package fr.maxairfrance.azplugin.bukkit.packets;

import fr.maxairfrance.azplugin.bukkit.AZManager;
import fr.maxairfrance.azplugin.bukkit.handlers.PopupType;
import fr.maxairfrance.azplugin.bukkit.utils.AZChatComponent;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import pactify.client.api.plsp.model.SimplePLSPRegex;
import pactify.client.api.plsp.packet.client.PLSPPacketPopupAlert;
import pactify.client.api.plsp.packet.client.PLSPPacketPopupConfirm;
import pactify.client.api.plsp.packet.client.PLSPPacketPopupPrompt;

public class PacketPopup {

    @Getter
    private final String name;
    @Getter
    private final PopupType type;
    @Getter
    private final PLSPPacketPopupAlert popupAlert;
    @Getter
    private final PLSPPacketPopupConfirm popupConfirm;
    @Getter
    private final PLSPPacketPopupPrompt popupPrompt;
    @Setter
    @Getter
    private AZChatComponent textComponent;
    @Setter
    @Getter
    private AZChatComponent okComponent;
    @Setter
    @Getter
    private AZChatComponent cancelComponent;
    @Setter
    private String defaultValue;
    @Setter
    @Getter
    private boolean password;

    public PacketPopup(String name, PopupType type) {
        this.name = name;
        this.type = type;
        this.popupAlert = new PLSPPacketPopupAlert();
        this.popupConfirm = new PLSPPacketPopupConfirm();
        this.popupPrompt = new PLSPPacketPopupPrompt();
    }

    public static void setPrompt(Player player, AZChatComponent text, AZChatComponent.ClickEvent clickEvent) {
        AZChatComponent okButton = new AZChatComponent("ok");

        PLSPPacketPopupPrompt popupPrompt = new PLSPPacketPopupPrompt(text, okButton, new AZChatComponent("cancel"), "", new SimplePLSPRegex(SimplePLSPRegex.Engine.RE2J, "(?s).*"), new SimplePLSPRegex(SimplePLSPRegex.Engine.RE2J, "(?s).*"), false);
        clickEvent.setValue(clickEvent.getValue() + " " + popupPrompt.getDefaultValue());
        okButton.setClickEvent(clickEvent);

        AZManager.sendPLSPMessage(player, popupPrompt);


    }

    public void send(Player player) {
        for (AZChatComponent extra : textComponent.getExtra()) {
            extra.setText(extra.getText().replaceAll("%player%", player.getName()));
        }
        switch (type) {
            case ALERT:
                okComponent.getClickEvent().setValue(okComponent.getClickEvent().getValue().replaceAll("%player%", player.getName()));
                popupAlert.setText(textComponent);
                popupAlert.setCloseButton(okComponent);
                AZManager.sendPLSPMessage(player, popupAlert);
                break;
            case CONFIRM:
                if (okComponent.getClickEvent() != null) {
                    okComponent.getClickEvent().setValue(okComponent.getClickEvent().getValue().replaceAll("%player%", player.getName()));
                }
                if (cancelComponent.getClickEvent() != null) {
                    cancelComponent.getClickEvent().setValue(cancelComponent.getClickEvent().getValue().replaceAll("%player%", player.getName()));
                }
                popupConfirm.setText(textComponent);
                popupConfirm.setOkButton(okComponent);
                popupConfirm.setCancelButton(cancelComponent);
                AZManager.sendPLSPMessage(player, popupConfirm);
                break;
            case PROMPT:

                defaultValue = defaultValue.replaceAll("%player%", player.getName());
                SimplePLSPRegex regex = new SimplePLSPRegex(SimplePLSPRegex.Engine.RE2J, "(?s).*");
                popupPrompt.setText(textComponent);
                popupPrompt.setDefaultValue(defaultValue);
                popupPrompt.setPassword(password);
                popupPrompt.setFinalRegex(regex);
                popupPrompt.setTypingRegex(regex);
                popupPrompt.setOkButton(okComponent);
                popupPrompt.setCancelButton(cancelComponent);
                if (okComponent.getClickEvent() != null) {
                    okComponent.getClickEvent().setValue(okComponent.getClickEvent().getValue().replaceAll("%player%", player.getName()));
                    okComponent.getClickEvent().setValue(okComponent.getClickEvent().getValue().replaceAll("%value%", popupPrompt.getDefaultValue()));
                }
                if (cancelComponent.getClickEvent() != null) {
                    cancelComponent.getClickEvent().setValue(cancelComponent.getClickEvent().getValue().replaceAll("%player%", player.getName()));
                    cancelComponent.getClickEvent().setValue(cancelComponent.getClickEvent().getValue().replaceAll("%value%", popupPrompt.getDefaultValue()));
                }

                AZManager.sendPLSPMessage(player, popupPrompt);
                break;
        }
    }
}
