package dev.vfyjxf.conduitstratus.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.IConduitDefinition;
import dev.vfyjxf.conduitstratus.api.conduit.network.INetwork;
import dev.vfyjxf.conduitstratus.api.conduit.network.INetworkNode;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;

public class Network implements INetwork {

    private final MutableList<INetworkNode> nodes = Lists.mutable.empty();

    @Override
    public ImmutableList<INetworkNode> getNodes() {
        return nodes.toImmutable();
    }

    @Override
    public boolean support(IConduitDefinition definition) {
        return false;
    }

    @Override
    public boolean updateNetwork() {
        return false;
    }

    @Override
    public void tick() {

    }
}
