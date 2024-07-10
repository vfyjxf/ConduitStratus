package dev.vfyjxf.conduitstratus.api.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.IConduit;

public interface INetworkNode {

    void setNetwork(IConduitNetwork network);

    IConduitNetwork getNetwork();

    void addConnection(INetworkNode node);

    void addConduit(IConduit<?> conduit);

}
