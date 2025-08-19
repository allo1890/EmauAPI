package fr.maxairfrance.azplugin.bukkit.utils;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pactify.client.api.mcprotocol.NotchianPacketBuffer;
import pactify.client.api.mcprotocol.model.NotchianItemStack;
import pactify.client.api.mcprotocol.model.NotchianNbtTagCompound;

public class AZItemStack implements NotchianItemStack {

    private int id;
    private int amount;
    private int damage;
    private NotchianNbtTagCompound tag;

    public AZItemStack(ItemStack itemStack) {
        this.id = itemStack.getTypeId();
        this.amount = itemStack.getAmount();
        this.damage = itemStack.getDurability();
        this.tag = null;
    }

    @Override
    public int getItemId() {
        return id;
    }

    @Override
    public int getCount() {
        return amount;
    }

    @Override
    public int getDamage() {
        return damage;
    }

    @Override
    @Nullable
    public NotchianNbtTagCompound getTag() {
        return tag;
    }

    @Override
    @NotNull
    public NotchianItemStack shallowClone() {
        AZItemStack clone = new AZItemStack();
        clone.id = this.id;
        clone.amount = this.amount;
        clone.damage = this.damage;
        clone.tag = this.tag;
        return clone;
    }

    @Override
    @NotNull
    public NotchianItemStack deepClone() {
        AZItemStack clone = new AZItemStack();
        clone.id = this.id;
        clone.amount = this.amount;
        clone.damage = this.damage;
        clone.tag = this.tag != null ? this.tag.deepClone() : null;
        return clone;
    }

    private AZItemStack() {}
}