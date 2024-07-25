package dev.vfyjxf.conduitstratus.api.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.IConduitDefinition;
import dev.vfyjxf.conduitstratus.api.conduit.ITypeDefinition;
import org.eclipse.collections.api.list.ImmutableList;
import org.jetbrains.annotations.Nullable;

public interface INetwork {

    ImmutableList<INetworkNode> getNodes();

    boolean support(IConduitDefinition definition);

    @Nullable
    <T> ImmutableList<IConduitChannel> getTypedChannels(ITypeDefinition<T> type);

    boolean updateNetwork();

    void tick();

}
