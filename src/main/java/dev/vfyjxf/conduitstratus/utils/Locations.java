package dev.vfyjxf.conduitstratus.utils;

import dev.vfyjxf.conduitstratus.Constants;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Rename {@link ResourceLocation}'s factory methods.
 */
@ApiStatus.Internal
public final class Locations {

    public static ResourceLocation parse(String location) {
        return ResourceLocation.parse(location);
    }

    public static @Nullable ResourceLocation maybe(String location) {
        return ResourceLocation.tryParse(location);
    }

    public static ResourceLocation create(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

    public static ResourceLocation ofMc(String path) {
        return ResourceLocation.withDefaultNamespace(path);
    }

    public static ResourceLocation of(String path) {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, path);
    }

    private Locations() {
    }
}
