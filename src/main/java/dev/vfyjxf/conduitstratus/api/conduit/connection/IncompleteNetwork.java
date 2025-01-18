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

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class IncompleteNetwork {
    private final AtomicInteger buildIndex = new AtomicInteger(1);
    private final MutableMap<ConduitNodeId, CachedNode> nodes = Maps.mutable.empty();
    private final UUID id = UUID.randomUUID();
    private final MutableMultimap<ResourceKey<Level>, ChunkPos> chunks = Multimaps.mutable.set.empty();

    private final AtomicBoolean cancelled = new AtomicBoolean(false);

    private ImmutableList<ConduitNodeId> nodeById;
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

    public UUID id() {
        return id;
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

    public CachedNode getNode(ConduitNodeId id) {
        return nodes.get(id);
    }

    public ImmutableList<ConduitNodeId> getNodes() {
        return nodeById;
    }


    public void addNode(CachedNode node) {
        if (prepared) {
            throw new IllegalStateException("Network is already frozen");
        }
        nodes.put(node.getId(), node);
    }


    public List<ConduitNodeId> cachedNeighbors(ConduitNodeId nodeId) {
        CachedNode node = nodes.get(nodeId);
        if (node == null) {
            return null;
        }
        return node.getNeighbors();
    }


    public void prepare() {

        if (nodes.size() > Short.MAX_VALUE) {
            throw new IllegalArgumentException("Too many nodes");
        }

        // distances 是一个半对称矩阵，用于存储节点之间的距离（不含对角线，下半部分）
        nodeById = Lists.immutable.ofAll(nodes.keySet());
        distances = new short[nodeById.size() * (nodeById.size() - 1) / 2];
        for (int i = 0; i < nodeById.size(); i++) {
            ConduitNodeId id = nodeById.get(i);
            nodes.get(id).setInternalId((short) i);
        }

        prepared = true;
    }

    @Nullable
    public ConduitNodeId nextToBuild() {
        if (cancelled.get()) {
            return null;
        }
        if (buildIndex.get() >= nodeById.size() - 1) {
            return null;
        }
        return nodeById.get(buildIndex.getAndIncrement());
    }

    public boolean finished() {
        return buildIndex.get() >= nodeById.size() - 1;
    }


    public Network build() {
        if (cancelled.get()) {
            return null;
        }
        if (!finished()) {
            throw new IllegalStateException("Network is not finished");
        }

        Object2ShortMap<ConduitNodeId> nodeIndex = new Object2ShortOpenHashMap<>(nodeById.size());
        for (int i = 0; i < nodeById.size(); i++) {
            nodeIndex.put(nodeById.get(i), (short) i);
        }

        ConduitDistance distance = new ConduitDistance(distances, nodeIndex);

        return NetworkBuilder.buildNetwork(nodeById, distance);
    }

    public void setDistance(short from, short to, int distance) {
        if (from == to) {
            throw new IllegalArgumentException("from == to");
        }
        int index = ConduitDistance.getDistanceIndex(from, to, nodeById.size());
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
