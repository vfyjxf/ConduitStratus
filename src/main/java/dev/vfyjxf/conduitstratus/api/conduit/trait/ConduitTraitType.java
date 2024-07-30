package dev.vfyjxf.conduitstratus.api.conduit.trait;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.eclipse.collections.api.set.ImmutableSet;
import org.jetbrains.annotations.Nullable;

public interface ConduitTraitType<T> {


    ImmutableSet<BlockCapability<?, @Nullable Direction>> receiverCapabilities();

    ImmutableSet<BlockCapability<?, @Nullable Direction>> handlerCapabilities();

}
