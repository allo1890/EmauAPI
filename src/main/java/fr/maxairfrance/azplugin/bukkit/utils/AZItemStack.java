package fr.maxairfrance.azplugin.bukkit.utils;

import org.bukkit.inventory.ItemStack;
import pactify.client.api.mcprotocol.NotchianPacketBuffer;
import pactify.client.api.mcprotocol.model.NotchianItemStack;

public class AZItemStack implements NotchianItemStack {
    private final int id;
    private final byte amount;
    private final short damage;

    public AZItemStack(ItemStack itemStack) {
        this.id = itemStack.getTypeId();
        this.amount = (byte)itemStack.getDurability();
        this.damage = itemStack.getDurability();
    }

    public void write(NotchianPacketBuffer notchianPacketBuffer) {
        notchianPacketBuffer.writeShort(276);
        notchianPacketBuffer.writeByte(1);
        notchianPacketBuffer.writeShort(0);
        notchianPacketBuffer.writeByte(0);
    }

    public NotchianItemStack shallowClone() {
        try {
            return (AZItemStack)super.clone();
        } catch (CloneNotSupportedException var2) {
            CloneNotSupportedException e = var2;
            throw new RuntimeException(e);
        }
    }

    public NotchianItemStack deepClone() {
        return null;
    }
}
