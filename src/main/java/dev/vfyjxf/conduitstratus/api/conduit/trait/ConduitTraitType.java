package dev.vfyjxf.conduitstratus.api.conduit.trait;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.set.MutableSet;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class ConduitTraitType<T extends ConduitTrait<T>> {

    @SuppressWarnings("unchecked")
    public static <T extends ConduitTrait<T>> ConduitTraitType<T> create(
            MapCodec<T> codec,
            BlockCapability<?, @Nullable Direction> handleCapability
    ) {
        return new ConduitTraitType<>(codec, Sets.mutable.of(handleCapability), Sets.mutable.empty());
    }

    @SuppressWarnings("unchecked")
    public static <T extends ConduitTrait<T>> ConduitTraitType<T> create(
            MapCodec<T> codec,
            BlockCapability<?, @Nullable Direction> handleCapability,
            BlockCapability<?, @Nullable Direction> proxyCapability
    ) {
        return new ConduitTraitType<>(codec, Sets.mutable.of(handleCapability), Sets.mutable.of(proxyCapability));
    }

    public static <T extends ConduitTrait<T>> ConduitTraitType<T> create(
            MapCodec<T> codec,
            Set<BlockCapability<?, @Nullable Direction>> handleCapabilities,
            Set<BlockCapability<?, @Nullable Direction>> proxyCapabilities
    ) {
        return new ConduitTraitType<>(codec, Sets.mutable.ofAll(handleCapabilities), Sets.mutable.ofAll(proxyCapabilities));
    }

    private final MapCodec<T> codec;
    private final MutableSet<BlockCapability<?, @Nullable Direction>> handleCapabilities;
    private final MutableSet<BlockCapability<?, @Nullable Direction>> proxyCapabilities;

    public ConduitTraitType(
            MapCodec<T> codec,
            MutableSet<BlockCapability<?, @Nullable Direction>> handleCapabilities,
            MutableSet<BlockCapability<?, @Nullable Direction>> proxyCapabilities
    ) {
        this.codec = codec;
        this.handleCapabilities = handleCapabilities;
        this.proxyCapabilities = proxyCapabilities;
    }

    public MapCodec<T> codec() {
        return codec;
    }

    public MutableSet<BlockCapability<?, @Nullable Direction>> handleCapabilities() {
        return handleCapabilities.clone();
    }

    public MutableSet<BlockCapability<?, @Nullable Direction>> proxyCapabilities() {
        return proxyCapabilities.clone();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConduitTraitType<?> that = (ConduitTraitType<?>) o;
        return codec.equals(that.codec) && handleCapabilities.equals(that.handleCapabilities) && proxyCapabilities.equals(that.proxyCapabilities);
    }

    @Override
    public int hashCode() {
        int result = codec.hashCode();
        result = 31 * result + handleCapabilities.hashCode();
        result = 31 * result + proxyCapabilities.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ConduitTraitType{" +
                ", handleCapabilities=" + handleCapabilities +
                ", proxyCapabilities=" + proxyCapabilities +
                '}';
    }
}
