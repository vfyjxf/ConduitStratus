package dev.vfyjxf.conduitstratus.api.conduit.connection;

import dev.vfyjxf.conduitstratus.init.values.ModValues;
import dev.vfyjxf.conduitstratus.utils.StepTimer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ConnectionCalculation {

    private static final Logger log = LoggerFactory.getLogger(ConnectionCalculation.class);

    private record ConnectingNode(ConduitNodeId id, NodeBFSIterator bfs, IncompleteNetwork network) {
    }

    private record ComputingNode(short internalId, FastNodeBFSIterator bfs, IncompleteNetwork network) {
    }

    private static class ScheduledNode implements Comparable<ScheduledNode> {
        private final ConduitNode node;
        private final long time;
        private boolean cancelled = false;

        public ScheduledNode(ConduitNode node, long time) {
            this.node = node;
            this.time = time;
        }

        @Override
        public int compareTo(ScheduledNode o) {
            return Long.compare(time, o.time);
        }

        public void cancel() {
            this.cancelled = true;
        }

    }

    private final MutableMap<UUID, IncompleteNetwork> incompleteNetworks = Maps.mutable.empty();

    // 正在进行的计算
    private final ReentrantLock computeLock = new ReentrantLock();
    private final Condition computeCondition = computeLock.newCondition();

    private final ArrayDeque<IncompleteNetwork> pendingNetworks = new ArrayDeque<>();
    private final List<IncompleteNetwork> finishedNetworks = new ArrayList<>();

    // 用于连接计算的迭代器
    private final ComputingNode[] computingNodes;
    private final Thread[] computeThreads;

    private ConnectingNode collectingNode = null;
    private MinecraftServer server = null;

    // 节点支持延迟连接


    private final LinkedHashMap<ConduitNodeId, ConduitNode> idleNodes = new LinkedHashMap<>();
    private final PriorityQueue<ScheduledNode> schedulingIdleNodes = new PriorityQueue<>();
    private final HashMap<ConduitNodeId, ScheduledNode> schedulingIdleNodesMap = new HashMap<>();


    private static final ConnectionCalculation instance = new ConnectionCalculation();

    public static ConnectionCalculation getInstance() {
        return instance;
    }

    public void init() {
        NeoForge.EVENT_BUS.addListener(this::onServerStart);
        NeoForge.EVENT_BUS.addListener(this::onServerStop);
        NeoForge.EVENT_BUS.addListener(this::onServerTick);

    }

    private final int MAX_CONNECTION_PARALLEL = Runtime.getRuntime().availableProcessors();

    private void onServerStart(ServerStartedEvent event) {
        this.server = event.getServer();
        this.pendingNetworks.clear();
        this.finishedNetworks.clear();
        this.incompleteNetworks.clear();
        this.idleNodes.clear();
        this.schedulingIdleNodes.clear();
        this.tickNetworkTimer.clear();
        this.collectingNode = null;
        for (int i = 0; i < MAX_CONNECTION_PARALLEL; i++) {
            computingNodes[i] = null;
        }
        for (int i = 0; i < MAX_CONNECTION_PARALLEL; i++) {
            final int threadId = i;
            computeThreads[threadId] = new Thread(() -> {
                while (true) {

                    computeLock.lock();
                    try {
                        computeCondition.await();
                    } catch (InterruptedException e) {
                        log.error("Thread interrupted", e);
                        return;
                    } finally {
                        computeLock.unlock();
                    }

                    while (tickDistanceCompute(threadId)) {
                        Thread.yield();
                    }
                }
            });
            computeThreads[threadId].setDaemon(true);
            computeThreads[threadId].start();
        }
    }

    private void onServerStop(ServerStoppedEvent event) {
        computeLock.lock();
        try {
            for (IncompleteNetwork network : pendingNetworks) {
                network.destroy();
            }

            for (IncompleteNetwork network : finishedNetworks) {
                network.destroy();
            }

            for (ComputingNode node : computingNodes) {
                if (node != null) {
                    node.network().destroy();
                }
            }

        } finally {
            computeLock.unlock();
        }
        for (Thread thread : computeThreads) {
            try {
                thread.interrupt();
                thread.join();
            } catch (InterruptedException e) {
                log.error("Thread interrupted", e);
            }
        }
        this.server = null;
    }

    private void onServerTick(LevelTickEvent.Post event) {
        if (event.getLevel().isClientSide) {
            return;
        }
        tickNetwork();
        finishNetworks();
    }


    public void addIdleNode(ConduitNode node, int delay) {
        ScheduledNode scheduledNode = schedulingIdleNodesMap.remove(node.conduitId());
        if (scheduledNode != null) {
            scheduledNode.cancel();
        }
        ScheduledNode scheduled = new ScheduledNode(node, this.ticks + delay);
        schedulingIdleNodes.add(scheduled);
        schedulingIdleNodesMap.put(node.conduitId(), scheduled);
    }


    private static final int MAX_COLLECT_ITER_PER_TICK = 500;
    private static final int MAX_COMPUTE_ITER_PER_TICK = 1000;


    public ConnectionCalculation() {
        // 单线程先
        computingNodes = new ComputingNode[MAX_CONNECTION_PARALLEL];
        computeThreads = new Thread[MAX_CONNECTION_PARALLEL];
    }


    private void prepareNetwork(IncompleteNetwork network, NodeBFSIterator bfs) {
        if (network.isCancelled()) {
            log.info("Network {} is cancelled loading", network.uuid());
            network.destroy();
            return;
        }

        if (!network.prepare()) {
            log.error("Network {} is too large", network.uuid());
            network.destroy();
            return;
        }

        updateDistance(network, bfs);

        computeLock.lock();
        try {
            pendingNetworks.add(network);
            computeCondition.signalAll();
        } finally {
            computeLock.unlock();
        }
    }


    private void updateDistance(IncompleteNetwork network, NodeBFSIterator bfs) {
        List<NodeBFSIterator.IterNode> nodes = bfs.getNodes();
        if (nodes.isEmpty()) {
            return;
        }

        CachedNode cache = network.getNode(nodes.getFirst().nodeId);

        if (cache == null) {
            log.error("Trying to update distance for a node that is not in cache: node {}", nodes.getFirst().nodeId);
            throw new IllegalStateException("Trying to update distance for a node that is not in cache");
        }

        short fromIndex = cache.getInternalId();

        if (fromIndex == -1) {
            throw new IllegalStateException("Trying to update distance for a node that is not in network");
        }

        for (int i = 1; i < nodes.size(); i++) {
            NodeBFSIterator.IterNode iterNode = nodes.get(i);
            short toIndex = network.getNodeIdIndex(iterNode.nodeId);
            if (toIndex == -1) {
                throw new IllegalStateException("Trying to update distance for a node that is not in network");
            }
            network.setDistance(fromIndex, toIndex, iterNode.distance);
        }

    }

    private void updateDistance(IncompleteNetwork network, FastNodeBFSIterator bfs) {
        var nodes = bfs.getNodes();


        if (nodes.isEmpty()) {
            return;
        }


        long packedFirst = nodes.getFirst();
        CachedNode cache = network.getNode(FastNodeBFSIterator.unpackNodeId(packedFirst));

        short fromIndex = cache.getInternalId();

        if (fromIndex == -1) {
            throw new IllegalStateException("Trying to update distance for a node that is not in network");
        }

        for (int i = 1; i < nodes.size(); i++) {
            long packed = nodes.get(i);
            short toIndex = FastNodeBFSIterator.unpackNodeId(packed);
            if (toIndex == -1) {
                throw new IllegalStateException("Trying to update distance for a node that is not in network");
            }
            network.setDistance(fromIndex, toIndex, FastNodeBFSIterator.unpackDistance(packed));
        }
    }

    private ConduitNode searchNode(ConduitNodeId nodeId) {
        Level level = server.getLevel(nodeId.dimension());
        if (level == null) {
            return null;
        }
        return level.getCapability(ModValues.CONDUIT_NODE_CAP, nodeId.pos());
    }


    private List<ConduitNodeId> searchNeighbors(IncompleteNetwork network, ConduitNodeId nodeId) {
        CachedNode cachedNode = network.getNode(nodeId);
        if (cachedNode != null) {
            return cachedNode.getNeighbors();
        }

        ServerLevel level = server.getLevel(nodeId.dimension());
        if (level == null) {
            log.warn("Node {} is in unavailable dimension", nodeId);
            return null;
        }

        ChunkPos chunkPos = new ChunkPos(nodeId.pos());

        network.addChunkLoad(level, chunkPos);

        ConduitNode node = level.getCapability(ModValues.CONDUIT_NODE_CAP, nodeId.pos());

        if (node == null) {
            log.warn("Node {} is not available", nodeId);
            return null;
        }

        if (node.getNetwork() != null) {
            log.warn("Node {} is already in a network", nodeId);
            node.getNetwork().destroy();
            return null;
        }

        List<ConduitNodeId> neighbors = node.adjacentNodes();

        network.addNode(new CachedNode(nodeId, neighbors));
        node.setNetwork(network);
        return neighbors;
    }

    public void finishNetworks() {

        // update finished network
        ImmutableList<IncompleteNetwork> networks;
        try {
            computeLock.lock();
            if (finishedNetworks.isEmpty()) {
                return;
            }
            networks = Lists.immutable.ofAll(finishedNetworks);
            finishedNetworks.clear();
        } finally {
            computeLock.unlock();
        }


        for (IncompleteNetwork network : networks) {
            if (network.isCancelled()) {
                continue;
            }
            log.info("Building network {} with {} nodes", network.uuid(), network.getNodes().size());
            network.build(this.server);
            network.clearChunkLoad();
            log.info("Network {} is built", network.uuid());
        }
    }

    public ConduitNodeId takeIdleNode() {
        var it = idleNodes.sequencedEntrySet().iterator();

        while (it.hasNext()) {
            var entry = it.next();
            var node = entry.getValue();

            if (node.isInvalid() || node.getNetwork() != null) {
                it.remove();
                continue;
            }

            return entry.getKey();
        }

        return null;
    }


    private final StepTimer tickNetworkTimer = new StepTimer();

    private void finishTimer(String task) {
        tickNetworkTimer.stop();
        tickNetworkTimer.print(log, task);
        tickNetworkTimer.clear();
    }

    private long ticks = 0;

    public void tickNetwork() {
        if (server == null) {
            return;
        }

        while (!schedulingIdleNodes.isEmpty()) {
            if (schedulingIdleNodes.peek().time > ticks) {
                break;
            }
            ScheduledNode node = schedulingIdleNodes.poll();
            if(node.cancelled) {
                continue;
            }
            ConduitNodeId nodeId = node.node.conduitId();
            schedulingIdleNodesMap.remove(nodeId);
            idleNodes.remove(nodeId);
            idleNodes.put(nodeId, node.node);
        }

        ticks++;


        int iter = 0;
        processing:
        while (true) {
            tickNetworkTimer.start();
            if (collectingNode == null) {
                ConduitNodeId nodeId = takeIdleNode();

                if (nodeId == null) {

                    return;
                }
                // find next network

                IncompleteNetwork network = new IncompleteNetwork(server);

                NodeBFSIterator bfs = new NodeBFSIterator((id) -> this.searchNeighbors(network, id), nodeId);
                collectingNode = new ConnectingNode(nodeId, bfs, network);

            }

            ConnectingNode current = collectingNode;
            while (!current.network().isCancelled() && current.bfs().hasNext()) {
                if (iter >= MAX_COLLECT_ITER_PER_TICK) {
                    tickNetworkTimer.stop();
                    return;
                }
                iter++;
                if (!current.bfs().next()) {
                    break;
                }
            }

            if (current.network().isCancelled()) {
                log.warn("Network {} has cancelled", current.network().uuid());
                finishTimer("Preparing network");
                collectingNode = null;
                continue processing;
            }

            for (NodeBFSIterator.IterNode iterNode : current.bfs().getNodes()) {
                idleNodes.remove(iterNode.nodeId);
            }

            if (current.bfs().hasInvalid()) {
                HashSet<ConduitNodeId> invalidNodes = collectInvalidNodes(current);
                current.network().destroy();

                for (ConduitNodeId invalidNode : invalidNodes) {
                    ConduitNode node = searchNode(invalidNode);
                    if (node != null) {
                        node.validate();
                    }
                }

                collectingNode = null;
                log.warn("Network {} has invalid nodes, cancelled", current.network().uuid());
                finishTimer("Preparing network");
                continue processing;
            }

            prepareNetwork(current.network(), current.bfs());
            collectingNode = null;
            log.info("Prepared network with {} nodes, id: {}", current.network().getNodes().size(), current.network().uuid());
            finishTimer("Preparing network");

        }


    }

    private static @NotNull HashSet<ConduitNodeId> collectInvalidNodes(ConnectingNode current) {
        HashSet<ConduitNodeId> invalidNodes = new HashSet<>();
        invalidNodes.add(current.bfs().getNodes().getFirst().nodeId);
        for (NodeBFSIterator.IterNode iterNode : current.bfs().getNodes()) {
            if (iterNode.invalid) {
                invalidNodes.add(iterNode.nodeId);
                if (iterNode.fromId != null) {
                    invalidNodes.add(iterNode.fromId);
                }
            }
        }
        return invalidNodes;
    }

    private short takePendingNode() {

        while (true) {
            IncompleteNetwork network = pendingNetworks.peek();
            if (network == null) {
                return -1;
            }

            short nodeId = network.nextToBuild();
            if (nodeId > 0) {
                return nodeId;
            }

            Duration duration = Duration.between(network.getStartTime(), Instant.now());
            log.info("Network {} with {} nodes computed in {} ms", network.uuid(), network.getNodes().size(), duration.toNanos() / 1_000_000.0);

            // current network is finished
            finishedNetworks.add(network);
            pendingNetworks.poll();
        }
    }

    private boolean tickDistanceCompute(int threadId) {
        int iter = 0;
        processing:
        while (true) {
            if (computingNodes[threadId] == null) {
                short internalNodeId;
                IncompleteNetwork network;
                computeLock.lock();
                try {
                    internalNodeId = takePendingNode();
                    network = pendingNetworks.peek();
                } finally {
                    computeLock.unlock();
                }

                if (internalNodeId < 0 || network == null) {
                    return false;
                }

                FastNodeBFSIterator bfs = new FastNodeBFSIterator(network::cachedNeighbors, internalNodeId);
                computingNodes[threadId] = new ComputingNode(internalNodeId, bfs, network);
            }

            ComputingNode current = computingNodes[threadId];

            IncompleteNetwork network = current.network();
            FastNodeBFSIterator bfs = current.bfs();

            while (bfs.hasNext()) {
                if (iter >= MAX_COMPUTE_ITER_PER_TICK) {
                    return true;
                }
                if (network.isCancelled()) {
                    break;
                }
                current.bfs().next();
                iter++;
            }

            if (current.network().isCancelled()) {
                computingNodes[threadId] = null;
                continue processing;
            }
            // 计算完成，保存距离
            updateDistance(current.network(), current.bfs());

            computingNodes[threadId] = null;

        }

    }


}
