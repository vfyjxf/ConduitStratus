package dev.vfyjxf.conduitstratus.api.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.IConduitDefinition;
import dev.vfyjxf.conduitstratus.api.conduit.ITypeDefinition;
import org.eclipse.collections.api.list.ImmutableList;

//TODO:Should this existing?
public interface ITypedChannel<T> {

    ITypeDefinition<T> definition();

    ImmutableList<IConduitChannel> channels();

     IConduitChannel createChannel(IConduitDefinition definition);

}
