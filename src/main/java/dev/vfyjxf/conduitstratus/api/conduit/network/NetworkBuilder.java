package dev.vfyjxf.conduitstratus.api.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.connection.ConduitDistance;
import dev.vfyjxf.conduitstratus.api.conduit.connection.ConduitNodeId;
import dev.vfyjxf.conduitstratus.api.conduit.connection.IncompleteNetwork;
import dev.vfyjxf.conduitstratus.conduit.network.ConduitNetwork;
import dev.vfyjxf.conduitstratus.conduit.network.ManagedNetworkNode;
import dev.vfyjxf.conduitstratus.utils.tick.TickDispatcher;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.eclipse.collections.api.list.ImmutableList;

import java.util.List;
import java.util.UUID;

public final class NetworkBuilder {

    public static void onInitTick(BlockEntity blockEntity, Runnable runnable) {
        TickDispatcher.instance().addInit(blockEntity, runnable);
    }

    public static InitNetworkNode createInitNetworkNode(BlockEntity holder) {
        return new ManagedNetworkNode(holder);
    }

    public static ConduitNetwork buildNetwork(UUID uuid, MinecraftServer server, List<ConduitNodeId> nodeIds, ConduitDistance distance) {
       return ConduitNetwork.create(uuid, server, nodeIds, distance);
    }

    private NetworkBuilder() {

    }
}
