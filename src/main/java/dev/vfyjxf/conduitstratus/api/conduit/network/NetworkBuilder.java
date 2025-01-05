package dev.vfyjxf.conduitstratus.api.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.connection.ConduitDistance;
import dev.vfyjxf.conduitstratus.api.conduit.connection.ConduitNodeId;
import dev.vfyjxf.conduitstratus.api.conduit.connection.IncompleteNetwork;
import dev.vfyjxf.conduitstratus.conduit.network.ConduitNetwork;
import dev.vfyjxf.conduitstratus.conduit.network.ManagedNetworkNode;
import dev.vfyjxf.conduitstratus.utils.tick.TickDispatcher;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.eclipse.collections.api.list.ImmutableList;

public final class NetworkBuilder {

    public static void onInitTick(BlockEntity blockEntity, Runnable runnable) {
        TickDispatcher.instance().addInit(blockEntity, runnable);
    }

    public static InitNetworkNode createInitNetworkNode(BlockEntity holder) {
        return new ManagedNetworkNode(holder);
    }

    public static ConduitNetwork buildNetwork(ImmutableList<ConduitNodeId> nodeIds, ConduitDistance distance) {
       return ConduitNetwork.create(nodeIds, distance);
    }

    private NetworkBuilder() {

    }
}
