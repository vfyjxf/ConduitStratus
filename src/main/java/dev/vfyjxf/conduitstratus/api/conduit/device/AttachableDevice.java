package dev.vfyjxf.conduitstratus.api.conduit.device;

import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import dev.vfyjxf.conduitstratus.utils.LevelHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

/**
 * a small device attached to a conduit.
 */
//TODO:more contract and method
//TODO:Add Lifecycle Event and contract
public interface AttachableDevice extends Device {

    /**
     * @return the facing direction of the device.
     */
    Direction getDirection();

    default BlockPos getFacingPos() {
        return getNode().getPos().relative(getDirection());
    }

    /**
     * @return return loaded block entity at facing position.
     */
    @Nullable
    default BlockEntity getFacingBlockEntity() {
        NetworkNode node = getNode();
        Level level = node.getLevel();
        BlockPos target = node.getPos().relative(getDirection());
        return LevelHelper.getBlockEntity(level, target);
    }

    default void onAttach() {
    }


}
