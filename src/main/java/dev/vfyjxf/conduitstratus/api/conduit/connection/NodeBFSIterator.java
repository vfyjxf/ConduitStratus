package dev.vfyjxf.conduitstratus.api.conduit.connection;

import java.util.*;

public class NodeBFSIterator {

    public static class IterNode {
        public ConduitNodeId nodeId;
        public ConduitNodeId fromId;
        public boolean invalid;
        public int distance;
    }

    @FunctionalInterface
    public interface NeighborProvider {
        List<ConduitNodeId> getNeighbors(ConduitNodeId nodeId);
    }

    private final ArrayList<IterNode> queue = new ArrayList<>();
    private final HashSet<ConduitNodeId> visited = new HashSet<>();
    private final NeighborProvider neighborProvider;
    private int index;
    private boolean hasInvalid;

    public List<IterNode> getNodes() {
        return queue;
    }

    public boolean hasInvalid() {
        return hasInvalid;
    }

    public NodeBFSIterator(NeighborProvider neighborProvider, ConduitNodeId start) {
        Objects.requireNonNull(neighborProvider);
        Objects.requireNonNull(start);
        this.neighborProvider = neighborProvider;
        IterNode startNode = new IterNode();
        startNode.nodeId = start;
        startNode.fromId = null;
        queue.add(startNode);
    }

    public boolean hasNext() {
        return index < queue.size();
    }

    public boolean next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        IterNode current = queue.get(index++);
        visited.add(current.nodeId);

        List<ConduitNodeId> neighbors = neighborProvider.getNeighbors(current.nodeId);

        if (neighbors == null) {
            current.invalid = true;
            hasInvalid = true;
            return false;
        }

        for (ConduitNodeId neighborId : neighbors) {
            if (visited.contains(neighborId)) {
                continue;
            }
            IterNode neighbor = new IterNode();
            neighbor.nodeId = neighborId;
            neighbor.fromId = current.nodeId;
            neighbor.distance = current.distance + 1;
            queue.add(neighbor);
        }

        return true;
    }
}
