package dev.vfyjxf.conduitstratus.api.conduit.trait;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

public interface TraitConnection {

    @Nullable
    <T, C> T getCapability(BlockCapability<T, @Nullable C> capability, @Nullable C context);

    @Nullable
    <T> T getCapability(BlockCapability<T, @Nullable Direction> capability);

    @Nullable
    <T> T getCapabilityVoid(BlockCapability<T, @Nullable Void> capability);

    @Nullable
    BlockEntity getFacing();

    /**
     * @return the facing direction of the trait.
     */
    Direction getDirection();

    default Direction getOpposite() {
        return getDirection().getOpposite();
    }

    void destroy();

}
