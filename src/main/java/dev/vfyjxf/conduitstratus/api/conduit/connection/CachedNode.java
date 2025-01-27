package dev.vfyjxf.conduitstratus.api.conduit.connection;

import org.jetbrains.annotations.ApiStatus;

import java.util.List;

@ApiStatus.Internal
public class CachedNode {
    private final ConduitNodeId id;
    private final List<ConduitNodeId> neighbors;
    private short[] neighborInternalIds;
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

    public short[] getNeighborInternalIds() {
        return neighborInternalIds;
    }

    public void setNeighborInternalIds(short[] neighborInternalIds) {
        this.neighborInternalIds = neighborInternalIds;
    }
}
