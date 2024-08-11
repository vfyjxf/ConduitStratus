package dev.vfyjxf.conduitstratus.api.conduit.data;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public record NetworkContextType<T>(
        ResourceLocation uid,
        Codec<T> codec,
        Supplier<T> defaultFactory,
        BinaryOperator<T> mergeFunction,
        @Nullable UnaryOperator<T> copyFunction
) {
    public static <T> NetworkContextType<T> create(ResourceLocation uid, Codec<T> codec, Supplier<T> defaultFactory, BinaryOperator<T> mergeFunction, @Nullable UnaryOperator<T> copyFunction) {
        return new NetworkContextType<>(uid, codec, defaultFactory, mergeFunction, copyFunction);
    }
}
