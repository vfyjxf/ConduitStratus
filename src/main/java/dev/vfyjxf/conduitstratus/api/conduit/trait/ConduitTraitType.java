package dev.vfyjxf.conduitstratus.api.conduit.trait;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.eclipse.collections.api.set.ImmutableSet;
import org.jetbrains.annotations.Nullable;

public record ConduitTraitType<T extends ConduitTrait<T>>(
        MapCodec<T> codec,
        ImmutableSet<BlockCapability<?, @Nullable Direction>> receiverCapabilities,
        ImmutableSet<BlockCapability<?, @Nullable Direction>> handlerCapabilities
) {

}
