package dev.vfyjxf.conduitstratus.api.conduit.connection;

import dev.vfyjxf.conduitstratus.utils.FastBFSQueue;
import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;
import it.unimi.dsi.fastutil.longs.LongArrayFIFOQueue;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.api.list.primitive.LongList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.list.primitive.MutableLongList;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.list.mutable.primitive.LongArrayList;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.stream.Stream;

@ApiStatus.Internal
public class FastNodeBFSIterator {
    @FunctionalInterface
    public interface NeighborProvider {
        short[] getNeighbors(short nodeId);
    }

    public FastNodeBFSIterator(NeighborProvider neighborProvider, short start) {
        Objects.requireNonNull(neighborProvider);
        this.neighborProvider = neighborProvider;
        int startNode = pack(start, (short) 0);
        queue.enqueue(startNode);
    }

    public final FastBFSQueue queue = new FastBFSQueue(32);
    private final BitSet visited = new BitSet(Short.MAX_VALUE);
    private final MutableIntList nodes = new IntArrayList();
    private final NeighborProvider neighborProvider;

    public IntList getNodes() {
        return nodes;
    }

    private static int pack(short nodeId, short distance) {
        return ((distance & 0xFFFF) << 16) | (nodeId & 0xFFFF);
    }

    public static short unpackDistance(long packed) {
        return (short) (packed >> 16);
    }

    public static short unpackNodeId(long packed) {
        return (short) packed;
    }

    public boolean hasNext() {
        return queue.notEmpty();
    }

    public boolean next() {
        int packed = queue.dequeue();
        short currentId = unpackNodeId(packed);
        if(visited.get(currentId)) {
            return true;
        }
        visited.set(currentId);
        short currentDistance = unpackDistance(packed);
        nodes.add(packed);

        short[] neighbors = neighborProvider.getNeighbors(currentId);

        for (short neighborId : neighbors) {
            if (visited.get(neighborId)) {
                continue;
            }
            int neighbor = pack(neighborId, (short) (currentDistance + 1));
            queue.enqueue(neighbor);
        }

        return true;
    }
}
