package dev.vfyjxf.conduitstratus.api.conduit;

import dev.vfyjxf.conduitstratus.api.conduit.connection.ConduitNodeId;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.eclipse.collections.api.list.MutableList;

public interface ConduitEntity {

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
