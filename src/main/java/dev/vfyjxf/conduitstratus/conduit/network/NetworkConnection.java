package dev.vfyjxf.conduitstratus.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.network.INetwork;
import dev.vfyjxf.conduitstratus.api.conduit.network.INetworkConnection;
import dev.vfyjxf.conduitstratus.api.conduit.network.INetworkNode;
import net.minecraft.core.Direction;
import org.eclipse.collections.api.factory.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class NetworkConnection implements INetworkConnection, Iterable<INetworkNode> {

    private final INetworkNode left;
    private final INetworkNode right;
    private final INetwork network;

    public static NetworkConnection create(INetworkNode left, INetworkNode right) {
        return new NetworkConnection(left, right);
    }

    private NetworkConnection(INetworkNode left, INetworkNode right) {
        this.left = left;
        this.right = right;
        this.network = left.getNetwork();
    }

    @Override
    public INetwork getNetwork() {
        return network;
    }

    @Override
    public INetworkNode getOtherSide(INetworkNode networkNode) {
        return networkNode == left ? right : left;
    }

    @Override
    public Direction getDirection(INetworkNode sourceNode) {
        if (sourceNode != left && sourceNode != right) {
            throw new IllegalArgumentException("Source node must be either left or right");
        }
        return sourceNode.getConnectionsMap().detect((k, v) -> v == this).getOne();
    }

    @Override
    public void destroy() {

    }

    @Override
    public INetworkNode left() {
        return left;
    }

    @Override
    public INetworkNode right() {
        return right;
    }

    @NotNull
    @Override
    public Iterator<INetworkNode> iterator() {
        return Lists.mutable.of(left, right).iterator();
    }
}
