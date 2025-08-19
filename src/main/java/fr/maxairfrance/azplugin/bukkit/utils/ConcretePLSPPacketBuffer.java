package fr.maxairfrance.azplugin.bukkit.utils;

import org.jetbrains.annotations.Nullable;

public class ConcretePLSPPacketBuffer extends PLSPPacketBuffer {

    public ConcretePLSPPacketBuffer() {
        super();
    }

    @Override
    @Nullable
    public PLSPPacketBuffer markReaderIndex() {
        return this;
    }

    @Override
    @Nullable
    public PLSPPacketBuffer resetReaderIndex() {
        return this;
    }
}