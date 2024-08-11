package dev.vfyjxf.conduitstratus.api.conduit.network;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public record NetworkServiceType<T extends NetworkService>(Function<Network, T> factory, @Nullable Codec<T> codec) {
}
