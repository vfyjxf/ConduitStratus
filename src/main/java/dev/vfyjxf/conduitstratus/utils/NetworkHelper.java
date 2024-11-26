package dev.vfyjxf.conduitstratus.utils;

import dev.vfyjxf.conduitstratus.conduit.blockentity.ConduitBlockEntity;
import dev.vfyjxf.conduitstratus.conduit.network.ConduitNetworkNode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public final class NetworkHelper {

    @Nullable
    public static ConduitNetworkNode findNode(Level level, BlockPos pos) {
        if (level.isClientSide) {
            return null;
        }
        if (level.isLoaded(pos)) {
            if (level.getBlockEntity(pos) instanceof ConduitBlockEntity blockEntity) {
                return (ConduitNetworkNode) blockEntity.getNode();
            }

        }
        return null;
    }

    private NetworkHelper() {
    }
}
