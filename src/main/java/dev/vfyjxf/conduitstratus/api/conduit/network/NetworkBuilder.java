package dev.vfyjxf.conduitstratus.api.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.connection.ConduitDistance;
import dev.vfyjxf.conduitstratus.api.conduit.connection.ConduitNodeId;
import dev.vfyjxf.conduitstratus.conduit.network.ConduitNetwork;
import dev.vfyjxf.conduitstratus.utils.tick.TickDispatcher;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;
import java.util.UUID;

public final class NetworkBuilder {

    public static void onInitTick(BlockEntity blockEntity, Runnable runnable) {
        TickDispatcher.instance().addInit(blockEntity, runnable);
    }

    public static ConduitNetwork buildNetwork(UUID uuid, MinecraftServer server, List<ConduitNodeId> nodeIds, ConduitDistance distance) {
        return ConduitNetwork.create(uuid, server, nodeIds, distance);
    }

    private NetworkBuilder() {

    }
}
