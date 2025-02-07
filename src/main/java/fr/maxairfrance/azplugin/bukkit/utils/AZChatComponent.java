package fr.maxairfrance.azplugin.bukkit.utils;

import com.google.gson.*;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import pactify.client.api.mcprotocol.NotchianPacketBuffer;
import pactify.client.api.mcprotocol.model.NotchianChatComponent;
import pactify.client.api.mcprotocol.util.NotchianPacketUtil;

import java.util.ArrayList;
import java.util.List;

@Getter
public class AZChatComponent implements NotchianChatComponent {
    @Setter
    private String text;
    @Setter
    private ClickEvent clickEvent;
    @Setter
    private HoverEvent hoverEvent;
    private final List<AZChatComponent> extra;

    public AZChatComponent(String text) {
        this.text = text;
        this.extra = new ArrayList<>();
    }

    public AZChatComponent(TextComponent textComponent) {
        this.text = textComponent.getText();
        this.extra = new ArrayList<>();
        if (textComponent.getExtra() != null && textComponent.getExtra().size() != 0) {
            for (BaseComponent baseComponent : textComponent.getExtra()) {
                AZChatComponent azChatComponent = new AZChatComponent((TextComponent) baseComponent);
                this.extra.add(azChatComponent);
            }
        }
        if (textComponent.getClickEvent() != null) {
            this.clickEvent = new ClickEvent(textComponent.getClickEvent().getAction().name().toLowerCase(), textComponent.getClickEvent().getValue());
        }
        if (textComponent.getHoverEvent() != null) {
            StringBuilder sb = new StringBuilder();
            for (BaseComponent baseComponent : textComponent.getHoverEvent().getValue()) {
                sb.append(baseComponent.toLegacyText());
            }
            this.hoverEvent = new HoverEvent(textComponent.getHoverEvent().getAction().name().toLowerCase(), sb.toString());
        }
    }

    @Override
    public String toString() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("text", this.text);
        if (this.clickEvent != null) {
            JsonObject clickEventJson = new JsonObject();
            clickEventJson.addProperty("action", this.clickEvent.action);
            clickEventJson.addProperty("value", this.clickEvent.value);
            jsonObject.add("clickEvent", clickEventJson);
        }
        if (this.hoverEvent != null) {
            JsonObject hoverEventJson = new JsonObject();
            hoverEventJson.addProperty("action", this.hoverEvent.action);
            hoverEventJson.addProperty("value", this.hoverEvent.value);
            jsonObject.add("hoverEvent", hoverEventJson);
        }
        if (!this.extra.isEmpty()) {
            JsonArray extraArray = new JsonArray();
            for (AZChatComponent component : this.extra) {
                extraArray.add(new JsonParser().parse(component.toString()));
            }
            jsonObject.add("extra", extraArray);
        }

        return jsonObject.toString();
    }

    @Override
    public void write(NotchianPacketBuffer notchianPacketBuffer) {
        NotchianPacketUtil.writeString(notchianPacketBuffer, this.toString(), 9999);
    }

    @Override
    public NotchianChatComponent shallowClone() {
        try {
            return (AZChatComponent)super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public NotchianChatComponent deepClone() {
        return null;
    }

    @Setter
    public static class ClickEvent {
        private String action;
        @Getter
        private String value;
        public ClickEvent(String action, String value) {
            this.action = action;
            this.value = value;
        }

    }

    @Setter
    @Getter
    public static class HoverEvent {
        private String action;
        private String value;
        public HoverEvent(String action, String value) {
            this.action = action;
            this.value = value;
        }

    }
}
