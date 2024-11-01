package dev.vfyjxf.conduitstratus.api.conduit.data;

import net.minecraft.nbt.CompoundTag;

public interface DataKey<T> {

    String key();

    T defaultValue();

    void save(T value, CompoundTag data);

    T load(CompoundTag data);

}
