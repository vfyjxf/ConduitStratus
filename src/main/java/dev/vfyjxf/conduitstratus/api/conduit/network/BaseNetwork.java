package dev.vfyjxf.conduitstratus.api.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.connection.ConduitNodeId;

import java.util.List;
import java.util.UUID;

public interface BaseNetwork {
    UUID uuid();
    void destroy();
    List<ConduitNodeId> nodeIds();
    NetworkStatus status();

}
