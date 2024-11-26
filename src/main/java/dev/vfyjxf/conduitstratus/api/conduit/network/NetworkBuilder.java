package dev.vfyjxf.conduitstratus.api.conduit.network;

import dev.vfyjxf.conduitstratus.conduit.network.ConduitNetwork;
import dev.vfyjxf.conduitstratus.conduit.network.ConduitNetworkNode;
import dev.vfyjxf.conduitstratus.conduit.network.ConduitNodeConnection;
import dev.vfyjxf.conduitstratus.conduit.network.ManagedNetworkNode;
import dev.vfyjxf.conduitstratus.utils.tick.TickDispatcher;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.eclipse.collections.api.factory.Lists;

public final class NetworkBuilder {

    public static void onInitTick(BlockEntity blockEntity, Runnable runnable) {
        TickDispatcher.instance().addInit(blockEntity, runnable);
    }

    public static NodeConnection createConnection(NetworkNode leftNode, NetworkNode rightNode, Direction fromLeftToRight) {
        var left = (ConduitNetworkNode) leftNode;
        var right = (ConduitNetworkNode) rightNode;
        if (left.connected(rightNode) || right.connected(leftNode)) {
            throw new IllegalArgumentException("Nodes are already connected");
        }
        var connection = ConduitNodeConnection.create(left, right, fromLeftToRight);

        mergeOrCreate(left, right);
        left.addConnection(fromLeftToRight, connection);
        right.addConnection(fromLeftToRight.getOpposite(), connection);
        return connection;
    }

    private static void mergeOrCreate(ConduitNetworkNode left, ConduitNetworkNode right) {
        var first = left.getNetworkUnsafe();
        var second = right.getNetworkUnsafe();
        if (first == null && second == null) {
            ConduitNetwork network = ConduitNetwork.create();
            left.setNetwork(network);
            right.setNetwork(network);
            TickDispatcher.instance().addNetwork(network);
        } else if (first == null) {
            left.setNetwork(second);
        } else if (second == null) {
            right.setNetwork(first);
        } else if (first != second) {
            if (first.size() > second.size()) {
                moveNodes(second, first);
            } else {
                moveNodes(first, second);
            }
        }
    }

    private static void moveNodes(ConduitNetwork source, ConduitNetwork target) {
        for (ConduitNetworkNode node : Lists.mutable.withAll(source.getNodes())) {
            if (node.getNetwork() != target) {
                node.setNetwork(target);
            } else break;
        }
    }

    public static InitNetworkNode createInitNetworkNode(BlockEntity holder) {
        return new ManagedNetworkNode(holder);
    }

    private NetworkBuilder() {

    }

}
