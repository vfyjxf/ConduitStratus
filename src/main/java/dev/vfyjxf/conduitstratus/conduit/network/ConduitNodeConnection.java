package dev.vfyjxf.conduitstratus.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.network.Network;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import dev.vfyjxf.conduitstratus.api.conduit.network.NodeConnection;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.ApiStatus;

public class ConduitNodeConnection implements NodeConnection {

    private final ConduitNetworkNode left;
    private final ConduitNetworkNode right;
    private final Direction fromLeftToRight;

    @ApiStatus.Internal
    public static ConduitNodeConnection create(ConduitNetworkNode leftNode, ConduitNetworkNode rightNode, Direction fromLeftToRight) {
        return new ConduitNodeConnection(leftNode, rightNode, fromLeftToRight);
    }

    private ConduitNodeConnection(ConduitNetworkNode left, ConduitNetworkNode right, Direction fromLeftToRight) {
        this.left = left;
        this.right = right;
        this.fromLeftToRight = fromLeftToRight;
    }

    @Override
    public Network getNetwork() {
        return left.getNetwork();
    }

    @Override
    public ConduitNetworkNode getOtherSide(NetworkNode networkNode) {
        return networkNode == left ? right : left;
    }

    @Override
    public Direction getDirection(NetworkNode sourceNode) {
        if (sourceNode != left && sourceNode != right) {
            throw new IllegalArgumentException("Source node must be either first or second");
        }
        return sourceNode == left ? fromLeftToRight : fromLeftToRight.getOpposite();
    }

    @Override
    public void destroy() {
        //TODO: Implement it
    }

    @Override
    public ConduitNetworkNode first() {
        return left;
    }

    @Override
    public ConduitNetworkNode second() {
        return right;
    }

    @Override
    public ConduitNetworkNode one() {
        return left;
    }

    @Override
    public ConduitNetworkNode two() {
        return right;
    }

    @Override
    public ConduitNetworkNode left() {
        return left;
    }

    @Override
    public ConduitNetworkNode right() {
        return right;
    }

}
