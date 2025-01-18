package dev.vfyjxf.conduitstratus.api.conduit.connection;

import dev.vfyjxf.conduitstratus.api.conduit.network.Network;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkBuilder;
import it.unimi.dsi.fastutil.objects.Object2ShortMap;
import it.unimi.dsi.fastutil.objects.Object2ShortOpenHashMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.multimap.MutableMultimap;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.factory.Multimaps;
import org.eclipse.collections.impl.utility.Iterate;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class IncompleteNetwork {
    private final AtomicInteger buildIndex = new AtomicInteger(1);
    private final MutableMap<ConduitNodeId, CachedNode> nodes = Maps.mutable.empty();
    private final UUID uuid = UUID.randomUUID();
    private final MutableMultimap<ResourceKey<Level>, ChunkPos> chunks = Multimaps.mutable.set.empty();

    private final AtomicBoolean cancelled = new AtomicBoolean(false);

    private ConduitNodeId[] nodeIdMapping;
    private CachedNode[] nodeMapping;
    private short[] distances;
    private boolean prepared = false;

    public IncompleteNetwork() {
    }


    public boolean isCancelled() {
        return cancelled.get();
    }

    public void cancel() {
        cancelled.set(true);
    }

    public UUID uuid() {
        return uuid;
    }

    public void addChunk(ResourceKey<Level> dim, ChunkPos pos) {
        chunks.put(dim, pos);
    }

    public boolean hasChunk(ResourceKey<Level> dim, ChunkPos pos) {
        return chunks.get(dim).contains(pos);
    }

    public RichIterable<Pair<ResourceKey<Level>, RichIterable<ChunkPos>>> getChunks() {
        return chunks.keyMultiValuePairsView();
    }

    public CachedNode getNode(ConduitNodeId nodeId) {
        return nodes.get(nodeId);
    }

    public CachedNode getNode(short internalId) {
        return nodeMapping[internalId];
    }

    public ImmutableList<ConduitNodeId> getNodes() {
        return Lists.immutable.of(nodeIdMapping);
    }


    public void addNode(CachedNode node) {
        if (prepared) {
            throw new IllegalStateException("Network is already frozen");
        }
        nodes.put(node.getId(), node);
    }


    public short[] cachedNeighbors(short internalId) {
        CachedNode node = nodeMapping[internalId];
        return node.getNeighborInternalIds();
    }


    public void prepare() {

        if (nodes.size() > Short.MAX_VALUE) {
            throw new IllegalArgumentException("Too many nodes");
        }

        // distances 是一个半对称矩阵，用于存储节点之间的距离（不含对角线，下半部分）
        nodeIdMapping = Iterate.toArray(nodes.keySet(), new ConduitNodeId[0]);

        distances = new short[nodeIdMapping.length * (nodeIdMapping.length - 1) / 2];

        nodeMapping = new CachedNode[nodeIdMapping.length];

        for (int i = 0; i < nodeIdMapping.length; i++) {
            ConduitNodeId id = nodeIdMapping[i];
            CachedNode node = nodes.get(id);
            node.setInternalId((short) i);
            nodeMapping[i] = node;
        }

        for (int i = 0; i < nodeIdMapping.length; i++) {
            CachedNode node = nodeMapping[i];
            List<ConduitNodeId> neighbors = node.getNeighbors();
            short[] neighborInternalIds = new short[neighbors.size()];
            for (int j = 0; j < neighbors.size(); j++) {
                neighborInternalIds[j] = nodeMapping[getNodeIdIndex(neighbors.get(j))].getInternalId();
            }
            node.setNeighborInternalIds(neighborInternalIds);
        }


        prepared = true;
    }

    public short nextToBuild() {
        if (cancelled.get()) {
            return -1;
        }
        if (buildIndex.get() >= nodeIdMapping.length - 1) {
            return -1;
        }
        return (short) buildIndex.getAndIncrement();
    }

    public boolean finished() {
        return buildIndex.get() >= nodeIdMapping.length - 1;
    }


    public Network build() {
        if (cancelled.get()) {
            return null;
        }
        if (!finished()) {
            throw new IllegalStateException("Network is not finished");
        }

        Object2ShortMap<ConduitNodeId> nodeIndex = new Object2ShortOpenHashMap<>(nodeIdMapping.length);
        for (int i = 0; i < nodeIdMapping.length; i++) {
            nodeIndex.put(nodeIdMapping[i], (short) i);
        }

        ConduitDistance distance = new ConduitDistance(distances, nodeIndex);

        ImmutableList<ConduitNodeId> nodes = Lists.immutable.of(nodeIdMapping);
        return NetworkBuilder.buildNetwork(nodes, distance);
    }

    public void setDistance(short from, short to, int distance) {
        if (from == to) {
            throw new IllegalArgumentException("from == to");
        }
        int index = ConduitDistance.getDistanceIndex(from, to, nodeIdMapping.length);
        distances[index] = (short) distance;
    }

    public short getNodeIdIndex(ConduitNodeId nodeId) {
        var node = nodes.get(nodeId);
        if (node == null) {
            return -1;
        }
        return node.getInternalId();
    }

}
