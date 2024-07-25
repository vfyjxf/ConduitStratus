package dev.vfyjxf.conduitstratus.api.conduit.network;

import net.minecraft.core.Direction;

public interface INetworkConnection {

    INetwork getNetwork();

    INetworkNode getOtherSide(INetworkNode networkNode);

    Direction getDirection(INetworkNode sourceNode);

    void destroy();

    INetworkNode left();

    INetworkNode right();

}
