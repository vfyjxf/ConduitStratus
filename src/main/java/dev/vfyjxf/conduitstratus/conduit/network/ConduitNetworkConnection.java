package dev.vfyjxf.conduitstratus.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.network.Network;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import net.minecraft.core.Direction;
import org.eclipse.collections.api.factory.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class ConduitNetworkConnection implements dev.vfyjxf.conduitstratus.api.conduit.network.NetworkConnection, Iterable<NetworkNode> {

    private final NetworkNode left;
    private final NetworkNode right;
    private final Network network;

    public static ConduitNetworkConnection create(NetworkNode left, NetworkNode right) {
        return new ConduitNetworkConnection(left, right);
    }

    private ConduitNetworkConnection(NetworkNode left, NetworkNode right) {
        this.left = left;
        this.right = right;
        this.network = left.getNetwork();
    }

    @Override
    public Network getNetwork() {
        return network;
    }

    @Override
    public NetworkNode getOtherSide(NetworkNode networkNode) {
        return networkNode == left ? right : left;
    }

    @Override
    public Direction getDirection(NetworkNode sourceNode) {
        if (sourceNode != left && sourceNode != right) {
            throw new IllegalArgumentException("Source node must be either first or second");
        }
        return sourceNode.getConnectionsMap().detect((k, v) -> v == this).getOne();
    }

    @Override
    public void destroy() {

    }

    @Override
    public NetworkNode first() {
        return left;
    }

    @Override
    public NetworkNode second() {
        return right;
    }

    @NotNull
    @Override
    public Iterator<NetworkNode> iterator() {
        return Lists.mutable.of(left, right).iterator();
    }
}
