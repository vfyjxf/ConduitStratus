package dev.vfyjxf.conduitstratus.api.conduit.connection;

import org.eclipse.collections.api.list.MutableList;

public interface RemoteNodeProvider {
     boolean acceptsRemote(ConduitNodeId remote);

     boolean collectRemoteNodes(MutableList<ConduitNodeId> remoteNodes);
}
