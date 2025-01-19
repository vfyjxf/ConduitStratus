package dev.vfyjxf.conduitstratus.api.conduit.connection;

import dev.vfyjxf.conduitstratus.api.conduit.network.BaseNetwork;
import dev.vfyjxf.conduitstratus.api.conduit.network.Network;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkBuilder;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkStatus;
import dev.vfyjxf.conduitstratus.init.values.ModValues;
import it.unimi.dsi.fastutil.objects.Object2ShortMap;
import it.unimi.dsi.fastutil.objects.Object2ShortOpenHashMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
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
import org.eclipse.collections.impl.list.fixed.ArrayAdapter;
import org.eclipse.collections.impl.utility.Iterate;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class IncompleteNetwork implements BaseNetwork {
    private final AtomicInteger buildIndex = new AtomicInteger(1);
    private final MutableMap<ConduitNodeId, CachedNode> nodes = Maps.mutable.empty();
    private final UUID uuid = UUID.randomUUID();
    private final MutableMultimap<ResourceKey<Level>, ChunkPos> chunks = Multimaps.mutable.set.empty();
    private final MinecraftServer server;
    private static final TicketType<UUID> connectionTicketType = TicketType.create("conduit_stratus:connection_calculation", UUID::compareTo);


    private final AtomicBoolean cancelled = new AtomicBoolean(false);

    private ConduitNodeId[] nodeIdMapping;
    private CachedNode[] nodeMapping;
    private short[] distances;
    private boolean prepared = false;
    private boolean computeStart = false;
    private Instant startTime = null;

    public Instant getStartTime() {
        return startTime;
    }

    public IncompleteNetwork(MinecraftServer server) {
        this.server = server;
    }


    public boolean isCancelled() {
        return cancelled.get();
    }

    public void cancel() {
        cancelled.set(true);
    }

    @Override
    @NotNull
    public UUID uuid() {
        return uuid;
    }

    @Override
    public void destroy() {
        this.cancel();
        this.clearChunkLoad();
        this.nodes.clear();
        this.distances = null;
        this.nodeIdMapping = null;
        this.nodeMapping = null;

    }

    @Override
    @NotNull
    public List<ConduitNodeId> nodeIds() {
        if (cancelled.get()) {
            return Lists.mutable.empty();
        }
        if (computeStart) {
            return ArrayAdapter.adapt(nodeIdMapping);
        }
        return Lists.mutable.ofAll(nodes.keySet());
    }

    @Override
    @NotNull
    public NetworkStatus status() {
        if (this.cancelled.get()) {
            return NetworkStatus.Destroyed;
        }
        if (this.prepared) {
            return NetworkStatus.Computing;
        }

        return NetworkStatus.Constructing;
    }

    public void addChunkLoad(ServerLevel level, ChunkPos chunkPos) {
        if (computeStart) {
            throw new IllegalStateException("Nodes have been released");
        }
        ResourceKey<Level> dim = level.dimension();
        if (!this.hasChunk(dim, chunkPos)) {
            chunks.put(dim, chunkPos);
            level.getChunkSource().addRegionTicket(connectionTicketType, chunkPos, 0, this.uuid());
        }
    }

    public void clearChunkLoad() {
        for (var entry : this.getChunks()) {
            var level = server.getLevel(entry.getOne());
            if (level == null) {
                continue;
            }

            for (var pos : entry.getTwo()) {
                level.getChunkSource().removeRegionTicket(connectionTicketType, pos, 0, this.uuid());
            }
        }

        chunks.clear();
    }


    private boolean hasChunk(ResourceKey<Level> dim, ChunkPos pos) {
        return chunks.get(dim).contains(pos);
    }

    public RichIterable<Pair<ResourceKey<Level>, RichIterable<ChunkPos>>> getChunks() {
        return chunks.keyMultiValuePairsView();
    }

    public CachedNode getNode(ConduitNodeId nodeId) {
        if (computeStart) {
            throw new IllegalStateException("Nodes have been released");
        }
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


    public boolean prepare() {

        if (nodes.size() > Short.MAX_VALUE) {

            for(var nodeId : nodes.keysView()) {
                var level = server.getLevel(nodeId.dimension());
                if (level == null) {
                    continue;
                }

                var pos = nodeId.pos();

                var node = level.getCapability(ModValues.CONDUIT_NODE_CAP, pos);
                if (node == null) {
                    continue;
                }

                node.setNetwork(null);
                node.setInvalid();
            }
            return false;
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

        prepared = true;

        return true;
    }

    public void prepareInternalIds() {
        if (!prepared) {
            throw new IllegalStateException("Network is not prepared");
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
        computeStart = true;
        nodes.clear();
    }

    public short nextToBuild() {
        if (cancelled.get()) {
            return -1;
        }
        if (!computeStart) {
            this.startTime = Instant.now();
            prepareInternalIds();
        }
        if (buildIndex.get() >= nodeIdMapping.length - 1) {
            return -1;
        }
        return (short) buildIndex.getAndIncrement();
    }

    public boolean finished() {
        if (cancelled.get()) {
            return false;
        }
        return buildIndex.get() >= nodeIdMapping.length - 1;
    }


    public Network build(MinecraftServer server) {
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
        return NetworkBuilder.buildNetwork(this.uuid(), server, this.nodeIds(), distance);
    }

    public void setDistance(short from, short to, int distance) {
        if (cancelled.get()) {
            return;
        }
        if (from == to) {
            throw new IllegalArgumentException("from == to");
        }
        int index = ConduitDistance.getDistanceIndex(from, to, nodeIdMapping.length);
        distances[index] = (short) distance;
    }

    public short getNodeIdIndex(ConduitNodeId nodeId) {
        if (computeStart || cancelled.get()) {
            throw new IllegalStateException("Nodes have been released");
        }
        var node = nodes.get(nodeId);
        if (node == null) {
            return -1;
        }
        return node.getInternalId();
    }

}
