package dev.vfyjxf.conduitstratus.api.conduit.connection;

import it.unimi.dsi.fastutil.shorts.ShortArrayFIFOQueue;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.factory.primitive.ShortSets;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.primitive.MutableShortSet;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

@ApiStatus.Internal
public class FastNodeBFSIterator {

    public record IterNode(short nodeId, short fromId, short distance) {
    }

    @FunctionalInterface
    public interface NeighborProvider {
        short[] getNeighbors(short nodeId);
    }

    private final ArrayDeque<IterNode> queue = new ArrayDeque<>(32);
    private final BitSet visited = new BitSet(Short.MAX_VALUE);
    private final MutableList<IterNode> nodes = Lists.mutable.empty();
    private final NeighborProvider neighborProvider;
    private boolean hasInvalid;

    public List<IterNode> getNodes() {
        return nodes;
    }

    public boolean hasInvalid() {
        return hasInvalid;
    }

    public FastNodeBFSIterator(NeighborProvider neighborProvider, short start) {
        Objects.requireNonNull(neighborProvider);
        this.neighborProvider = neighborProvider;
        IterNode startNode = new IterNode(start, (short) -1, (short) 0);
        queue.offer(startNode);
    }

    public boolean hasNext() {
        return !queue.isEmpty();
    }

    public boolean next() {
        IterNode current = queue.poll();

        if(current == null) {
            throw new NoSuchElementException();
        }

        if(visited.get(current.nodeId)) {
            return true;
        }
        visited.set(current.nodeId);
        nodes.add(current);

        short[] neighbors = neighborProvider.getNeighbors(current.nodeId);

        for (short neighborId : neighbors) {
            if (visited.get(neighborId)) {
                continue;
            }
            IterNode neighbor = new IterNode(neighborId, current.nodeId, (short) (current.distance + 1));
            queue.offer(neighbor);
        }

        return true;
    }
}
