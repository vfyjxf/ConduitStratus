package dev.vfyjxf.conduitstratus.api.conduit.trait;

import dev.vfyjxf.conduitstratus.utils.LevelHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

public interface CapabilityTrait<CAP> extends Trait {

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
        return LevelHelper.getCapability(level, pos, getToken(), direction) != null;
    }

    @Override
    default boolean importable() {
        return getIO().input() && getCapability() != null;
    }

    @Override
    default boolean exportable() {
        return getIO().output() && getCapability() != null;
    }
}
