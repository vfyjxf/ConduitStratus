package dev.vfyjxf.conduitstratus.api.conduit.network;

import net.minecraft.core.Direction;

public interface NodeConnection {

    Network getNetwork();

    NetworkNode getOtherSide(NetworkNode networkNode);

    Direction getDirection(NetworkNode sourceNode);

    void destroy();

    NetworkNode first();

    NetworkNode second();

    NetworkNode one();

    NetworkNode two();

    NetworkNode left();

    NetworkNode right();

}
