package dev.vfyjxf.conduitstratus.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
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

    private LevelHelper() {
    }
}
