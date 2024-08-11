package dev.vfyjxf.conduitstratus.api.conduit.data;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.function.UnaryOperator;

public record NetworkDataType<T>(
        ResourceLocation uid,
        MapCodec<T> mapCodec,
        StreamCodec<RegistryFriendlyByteBuf, T> streamCodec,
        UnaryOperator<T> copyFunction
) {
    public static <T> NetworkDataType<T> create(ResourceLocation uid, MapCodec<T> mapCodec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec, UnaryOperator<T> copyFunction) {
        return new NetworkDataType<>(uid, mapCodec, streamCodec, copyFunction);
    }
}
