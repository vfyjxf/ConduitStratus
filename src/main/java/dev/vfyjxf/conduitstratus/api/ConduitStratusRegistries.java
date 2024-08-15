package dev.vfyjxf.conduitstratus.api;

import dev.vfyjxf.conduitstratus.Constants;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public final class ConduitStratusRegistries {


    public static <T> ResourceKey<Registry<T>> createKey(String path) {
        return ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, path));
    }

    public static <T> ResourceKey<Registry<T>> createKey(String namespace, String path) {
        return ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(namespace, path));
    }

    private ConduitStratusRegistries() {
    }
}
