package dev.vfyjxf.conduitstratus.api.conduit.trait;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.set.MutableSet;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class ConduitTraitType<T extends ConduitTrait<T>> {

    private final ResourceLocation uid;
    private final MutableSet<BlockCapability<?, @Nullable Direction>> proxyCapabilities = Sets.mutable.empty();
    @Nullable
    private final MapCodec<T> codec;

    public ConduitTraitType(
            ResourceLocation uid,
            Set<BlockCapability<?, @Nullable Direction>> proxyCapabilities,
            @Nullable MapCodec<T> codec
    ) {
        this.uid = uid;
        this.proxyCapabilities.addAll(proxyCapabilities);
        this.codec = codec;
    }

    public MutableSet<BlockCapability<?, @Nullable Direction>> proxyCapabilities() {
        return proxyCapabilities.clone();
    }

    public ResourceLocation uid() {
        return uid;
    }

    @Nullable
    public MapCodec<T> codec() {
        return codec;
    }

}
