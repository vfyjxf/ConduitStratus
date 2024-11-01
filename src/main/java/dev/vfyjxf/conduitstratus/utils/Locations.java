package dev.vfyjxf.conduitstratus.utils;

import dev.vfyjxf.conduitstratus.Constants;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * Rename {@link ResourceLocation}'s factory methods.
 */
public final class Locations {

    public static ResourceLocation parse(String location) {
        return ResourceLocation.parse(location);
    }

    public static @Nullable ResourceLocation maybe(String location) {
        return ResourceLocation.tryParse(location);
    }

    public static ResourceLocation of(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

    public static ResourceLocation ofMc(String path) {
        return ResourceLocation.withDefaultNamespace(path);
    }

    public static ResourceLocation create(String path) {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, path);
    }

    private Locations() {
    }
}
