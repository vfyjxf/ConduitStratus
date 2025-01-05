package dev.vfyjxf.conduitstratus.api.conduit.connection;

import it.unimi.dsi.fastutil.objects.Object2ShortMap;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.list.ImmutableList;

public class ConduitDistance {
    private final short[] distances;
    private final Object2ShortMap<ConduitNodeId> nodeIndex;

    public ConduitDistance(short[] distances, Object2ShortMap<ConduitNodeId> nodeIndex) {
        this.distances = distances;
        this.nodeIndex = nodeIndex;
    }

    public short getNodeIdIndex(ConduitNodeId nodeId) {
        return nodeIndex.getShort(nodeId);
    }


    public static int getDistanceIndex(short from, short to, int nodeCount) {
        if (from > to) {
            return getDistanceIndex(to, from, nodeCount);
        }
        return from * (2 * nodeCount - from - 1) / 2 + to - from - 1;
    }


    public short getDistance(ConduitNodeId from, ConduitNodeId to) {
        short fromIndex = nodeIndex.getShort(from);
        short toIndex = nodeIndex.getShort(to);
        if (fromIndex == -1 || toIndex == -1) {
            return -1;
        }

        if (fromIndex == toIndex) {
            return 0;
        }

        int index = getDistanceIndex(fromIndex, toIndex, nodeIndex.size());
        return distances[index];
    }
}
