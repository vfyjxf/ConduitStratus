package dev.vfyjxf.conduitstratus.api.conduit.network;

import net.minecraft.core.Direction;

public interface NetworkConnection {

    Network getNetwork();

    NetworkNode getOtherSide(NetworkNode networkNode);

    Direction getDirection(NetworkNode sourceNode);

    void destroy();

    NetworkNode left();

    NetworkNode right();

}
