package dev.vfyjxf.conduitstratus.api.conduit.trait;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

public interface CapabilityConduitTrait<T extends ConduitTrait<T>, CAP> extends ConduitTrait<T> {

    BlockCapability<? extends CAP, @Nullable Direction> getToken();

    @Nullable
    default CAP getCapability() {
        CapabilityConnection<CAP> connection = getConnection();
        return connection == null ? null : connection.getCapability();
    }

    @Nullable
    CapabilityConnection<CAP> getConnection();

    @Override
    default boolean connectable() {
        Level level = getLevel();
        BlockPos pos = getFacingPos();
        Direction direction = getDirection().getOpposite();
        return level.getCapability(getToken(), pos, direction) != null;
    }
}
