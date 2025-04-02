package dev.vfyjxf.conduitstratus.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

public class LevelHelper {

    /**
     * @param level    the level
     * @param blockPos the position of the block entity
     * @return if {@link Level#isLoaded(BlockPos)} returns false, this method will return null.
     */
    @Nullable
    public static BlockEntity getBlockEntity(Level level, BlockPos blockPos) {
        if (!level.isLoaded(blockPos)) {
            return null;
        }
        return level.getBlockEntity(blockPos);
    }

    public static <T extends BlockEntity> T getBlockEntity(Level level, BlockPos blockPos, Class<T> blockEntityClass) {
        BlockEntity blockEntity = getBlockEntity(level, blockPos);
        if (blockEntityClass.isInstance(blockEntity)) {
            return blockEntityClass.cast(blockEntity);
        }
        return null;
    }

    public static <CAP, CTX> CAP getCapability(Level level, BlockPos blockPos, BlockCapability<CAP, CTX> capability, CTX context) {
        if (level.isLoaded(blockPos)) {
            return level.getCapability(capability, blockPos, context);
        }
        return null;
    }

    private LevelHelper() {
    }

}
