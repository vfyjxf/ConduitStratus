package dev.vfyjxf.conduitstratus.api.data.lang;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public record LangEntry(String key, String value) {

    public MutableComponent get() {
        return Component.translatable(this.key);
    }

    public MutableComponent get(Object... args) {
        return Component.translatable(this.key, args);
    }

}
