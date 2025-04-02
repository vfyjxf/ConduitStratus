package dev.vfyjxf.conduitstratus.api;

import dev.vfyjxf.conduitstratus.StratusConstants;
import dev.vfyjxf.conduitstratus.api.conduit.DeviceType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegistryBuilder;

public final class StratusRegistries {

    public static final ResourceKey<Registry<DeviceType>> DEVICE_TYPE = createKey("device_type");

    public static final Registry<DeviceType> DEVICE_TYPE_REGISTRY = new RegistryBuilder<>(DEVICE_TYPE)
            .sync(true)
            .create();


    private static <T> ResourceKey<Registry<T>> createKey(String path) {
        return ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(StratusConstants.MOD_ID, path));
    }

    private static <T> ResourceKey<Registry<T>> createKey(String namespace, String path) {
        return ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(namespace, path));
    }

    private StratusRegistries() {
    }
}
