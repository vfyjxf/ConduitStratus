package dev.vfyjxf.conduitstratus.api.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.connection.ConduitNodeId;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.UUID;

@ApiStatus.Internal
public interface BaseNetwork {
    UUID uuid();

    void destroy();

    List<ConduitNodeId> nodeIds();

    NetworkStatus status();

}
