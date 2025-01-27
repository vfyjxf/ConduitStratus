package dev.vfyjxf.conduitstratus.api.conduit.connection;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.set.MutableSet;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

@ApiStatus.Internal
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

    private final ArrayDeque<IterNode> queue = new ArrayDeque<>();
    private final MutableSet<ConduitNodeId> visited = Sets.mutable.empty();
    private final MutableList<IterNode> nodes = Lists.mutable.empty();
    private final NeighborProvider neighborProvider;
    private boolean hasInvalid;

    public List<IterNode> getNodes() {
        return nodes;
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
        return !queue.isEmpty();
    }

    public boolean next() {
        IterNode current = queue.poll();

        if(current == null) {
            throw new NoSuchElementException();
        }

        if(visited.contains(current.nodeId)) {
            return true;
        }
        visited.add(current.nodeId);
        nodes.add(current);

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
            queue.offer(neighbor);
        }

        return true;
    }
}
