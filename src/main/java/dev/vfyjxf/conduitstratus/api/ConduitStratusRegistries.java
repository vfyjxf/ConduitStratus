package dev.vfyjxf.conduitstratus.api;

import dev.vfyjxf.conduitstratus.Constants;
import dev.vfyjxf.conduitstratus.api.conduit.ConduitType;
import dev.vfyjxf.conduitstratus.api.conduit.data.NetworkDataType;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkServiceType;
import dev.vfyjxf.conduitstratus.api.conduit.trait.ConduitTraitType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegistryBuilder;

public final class ConduitStratusRegistries {
    public static final ResourceKey<Registry<ConduitType>> CONDUIT_TYPE_KEY = createKey("conduit_type");
    public static final ResourceKey<Registry<ConduitTraitType<?>>> CONDUIT_TRAIT_TYPE_KEY = createKey("conduit_trait_type");
    public static final ResourceKey<Registry<NetworkDataType<?>>> NETWORK_DATA_TYPE_KEY = createKey("network_data_type");
    public static final ResourceKey<Registry<NetworkServiceType<?>>> NETWORK_SERVICE_TYPE_KEY = createKey("network_service_type");

    public static final Registry<ConduitType> CONDUIT_TYPE_REGISTRY = new RegistryBuilder<>(CONDUIT_TYPE_KEY).sync(true).create();
    public static final Registry<ConduitTraitType<?>> CONDUIT_TRAIT_TYPE_REGISTRY = new RegistryBuilder<>(CONDUIT_TRAIT_TYPE_KEY).sync(true).create();
    public static final Registry<NetworkDataType<?>> NETWORK_DATA_TYPE_REGISTRY = new RegistryBuilder<>(NETWORK_DATA_TYPE_KEY).sync(true).create();
    public static final Registry<NetworkServiceType<?>> NETWORK_SERVICE_TYPE_REGISTRY = new RegistryBuilder<>(NETWORK_SERVICE_TYPE_KEY).sync(true).create();

    public static <T> ResourceKey<Registry<T>> createKey(String path) {
        return ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, path));
    }

    public static <T> ResourceKey<Registry<T>> createKey(String namespace, String path) {
        return ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(namespace, path));
    }

    private ConduitStratusRegistries() {
    }
}
