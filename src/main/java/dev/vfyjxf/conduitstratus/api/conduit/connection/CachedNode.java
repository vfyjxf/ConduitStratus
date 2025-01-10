package dev.vfyjxf.conduitstratus.api.conduit.connection;

import java.util.List;

public class CachedNode {
    private final ConduitNodeId id;
    private final List<ConduitNodeId> neighbors;
    private short internalId;

    public CachedNode(ConduitNodeId id, List<ConduitNodeId> neighbors) {
        this.id = id;
        this.neighbors = neighbors;
        this.internalId = -1;
    }

    public ConduitNodeId getId() {
        return id;
    }

    public List<ConduitNodeId> getNeighbors() {
        return neighbors;
    }

    public short getInternalId() {
        return internalId;
    }

    public void setInternalId(short internalId) {
        this.internalId = internalId;
    }
}
