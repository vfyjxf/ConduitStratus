package dev.vfyjxf.conduitstratus.api.conduit;

import dev.vfyjxf.conduitstratus.api.conduit.connection.ConduitNode;
import dev.vfyjxf.conduitstratus.api.conduit.connection.ConduitNodeId;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.eclipse.collections.api.list.MutableList;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public interface ConduitEntity {

    @Nullable
    @Contract(pure = true)
    ConduitNode conduitNode();

    @Nullable
    @Contract(pure = true)
    NetworkNode networkNode();

    BlockEntity getBlockEntity();

    default BlockPos getPos() {
        return getBlockEntity().getBlockPos();
    }

    default ServerLevel serverLevel() {
        return (ServerLevel) getBlockEntity().getLevel();
    }

    Conduit getConduit();

    void setConduit(Conduit conduit);

    boolean acceptsNeighbor(Direction direction);

    void markForUpdate();

    void markForSave();

    void connectionChange();

    default boolean acceptsRemote(ConduitNodeId remote) {
        return false;
    }

    default boolean collectRemoteNodes(MutableList<ConduitNodeId> remoteNodes) {
        return false;
    }


}
