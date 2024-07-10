package dev.vfyjxf.conduitstratus.api.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.IConduitDefinition;
import org.eclipse.collections.api.list.ImmutableList;

public interface IConduitNetwork {

    ImmutableList<INetworkNode> getNodes();

    boolean support(IConduitDefinition<?> definition);

    boolean updateNetwork();

    void tick();

}
