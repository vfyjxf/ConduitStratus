package dev.vfyjxf.conduitstratus.api.conduit.connection;

import dev.vfyjxf.conduitstratus.init.values.ModValues;
import dev.vfyjxf.conduitstratus.utils.StepTimer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
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

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ConnectionCalculation {

    private static final Logger log = LoggerFactory.getLogger(ConnectionCalculation.class);

    private record ConnectingNode(ConduitNodeId id, NodeBFSIterator bfs, IncompleteNetwork network) {
    }

    private final MutableMap<UUID, IncompleteNetwork> incompleteNetworks = Maps.mutable.empty();

    // 正在进行的计算
    private final ReentrantLock computeLock = new ReentrantLock();
    private final Condition computeCondition = computeLock.newCondition();

    private final ArrayDeque<IncompleteNetwork> pendingNetworks = new ArrayDeque<>();
    private final List<IncompleteNetwork> finishedNetworks = new ArrayList<>();

    // 用于连接计算的迭代器
    private final ConnectingNode[] connectingNodes;

    private ConnectingNode collectingNode = null;
    private MinecraftServer server = null;

    private final TicketType<UUID> connectionTicketType = TicketType.create("conduit_stratus:connection_calculation", UUID::compareTo);

    public void cancelLoading(UUID id) {
        IncompleteNetwork network = incompleteNetworks.get(id);
        if (network == null) {
            return;
        }
        network.cancel();
    }


    private IncompleteNetwork createNetwork() {
        IncompleteNetwork network = new IncompleteNetwork();
        incompleteNetworks.put(network.id(), network);

        return network;
    }

    private void removeNetwork(UUID id) {
        IncompleteNetwork network = incompleteNetworks.remove(id);
        if (network == null) {
            return;
        }

        for (var entry : network.getChunks()) {
            var level = server.getLevel(entry.getOne());
            if (level == null) {
                continue;
            }

            for (var pos : entry.getTwo()) {
                level.getChunkSource().removeRegionTicket(connectionTicketType, pos, 0, network.id());
            }

        }
    }


    // 节点支持延迟连接


    private final LinkedHashMap<ConduitNodeId, ConduitNode> idleNodes = new LinkedHashMap<>();


    private static final ConnectionCalculation instance = new ConnectionCalculation();

    public static ConnectionCalculation getInstance() {
        return instance;
    }

    public void init() {
        NeoForge.EVENT_BUS.addListener(this::onServerStart);
        NeoForge.EVENT_BUS.addListener(this::onServerStop);
        NeoForge.EVENT_BUS.addListener(this::onServerTick);

    }


    private void onServerStart(ServerStartedEvent event) {
        this.server = event.getServer();
    }

    private void onServerStop(ServerStoppedEvent event) {
        this.server = null;
    }

    private void onServerTick(LevelTickEvent.Post event) {
        if (event.getLevel().isClientSide) {
            return;
        }
        tickNetwork();
//        tickConnect();
        finishNetworks();
    }


    public void addIdleNode(ConduitNode node) {

        idleNodes.put(node.conduitId(), node);
    }


    private static final int MAX_COLLECT_ITER_PER_TICK = 1000;
    private static final int MAX_COMPUTE_ITER_PER_TICK = 10000;


    public ConnectionCalculation() {
        final int MAX_CONNECTION_PARALLEL = Runtime.getRuntime().availableProcessors() + 1;
        // 单线程先
        connectingNodes = new ConnectingNode[MAX_CONNECTION_PARALLEL];
        Thread[] computeThreads = new Thread[MAX_CONNECTION_PARALLEL];

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

                    while (tickIdle(threadId)) {
                        Thread.yield();
                        // do nothing
                    }
                }
            });
            computeThreads[threadId].setDaemon(true);
            computeThreads[threadId].start();
        }
    }


    private void prepareNetwork(IncompleteNetwork network, NodeBFSIterator bfs) {
        if (network.isCancelled()) {
            log.info("Network {} is cancelled loading", network.id());
            removeNetwork(network.id());
            return;
        }
        network.prepare();

        updateDistance(network, bfs);

        computeLock.lock();
        try {
            pendingNetworks.add(network);
            computeCondition.signal();
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
        ConduitNodeId from = cache.getId();

        short fromIndex = network.getNodeIdIndex(from);

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

    private ConduitNode searchNode(ConduitNodeId nodeId) {
        Level level = server.getLevel(nodeId.dimension());
        if (level == null) {
            return null;
        }
        return level.getCapability(ModValues.CONDUIT_NODE_CAP, nodeId.pos());
    }


    private List<ConduitNodeId> searchNeighbors(IncompleteNetwork network, ConduitNodeId nodeId) {
        ServerLevel level = server.getLevel(nodeId.dimension());
        if (level == null) {
            log.warn("Node {} is in unavailable dimension", nodeId);
            return null;
        }

        ChunkPos chunkPos = new ChunkPos(nodeId.pos());
        if (!network.hasChunk(nodeId.dimension(), chunkPos)) {
            network.addChunk(nodeId.dimension(), chunkPos);
            level.getChunkSource().addRegionTicket(connectionTicketType, chunkPos, 0, network.id());
        }
        BlockEntity be = level.getBlockEntity(nodeId.pos());
        if (be == null) {
            log.warn("Node {} is not available", nodeId);
            return null;
        }

        ConduitNode node = level.getCapability(ModValues.CONDUIT_NODE_CAP, nodeId.pos());

        if (node == null) {
            log.warn("Node {} is not available", nodeId);
            return null;
        }

        List<ConduitNodeId> neighbors = node.adjacentNodes();

        network.addNode(new CachedNode(nodeId, neighbors));
        return neighbors;
    }

    public void tickConnect() {
        // TODO: 多线程
//        for (int i = 0; i < MAX_CONNECTION_PARALLEL; i++) {
//            tickIdle(i);
//        }
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
            log.info("Building network {} with {} nodes", network.id(), network.getNodes().size());
            network.build();
            removeNetwork(network.id());
            log.info("Network {} is built", network.id());
        }
    }

    public ConduitNodeId takeIdleNode() {
        var it = idleNodes.sequencedEntrySet().iterator();

        while (it.hasNext()) {
            var entry = it.next();
            var node = entry.getValue();

            if (node.initializing()) {
                continue;
            }

            if (!node.valid()) {
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

    public void tickNetwork() {
        if (server == null) {
            return;
        }


        int iter = 0;
        processing:
//        for(;;){
        while (true) {
            tickNetworkTimer.start();
            if (collectingNode == null) {
                ConduitNodeId nodeId = takeIdleNode();

                if (nodeId == null) {

                    return;
                }
                // find next network

                IncompleteNetwork network = createNetwork();

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

            for (NodeBFSIterator.IterNode iterNode : current.bfs().getNodes()) {
                idleNodes.remove(iterNode.nodeId);
            }

            if (current.bfs().hasInvalid()) {
                HashSet<ConduitNodeId> invalidNodes = collectInvalidNodes(current);

                for (ConduitNodeId invalidNode : invalidNodes) {
                    ConduitNode node = searchNode(invalidNode);
                    if (node != null) {
                        node.setValid(false);
                    }
                }

                removeNetwork(current.network().id());
                collectingNode = null;
                log.warn("Network {} has invalid nodes, cancelled", current.network().id());
                finishTimer("Preparing network");
                continue processing;
            }

            prepareNetwork(current.network(), current.bfs());
            collectingNode = null;
            log.info("Prepared network with {} nodes, id: {}", current.network().getNodes().size(), current.network().id());
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

    private ConduitNodeId takePendingNode() {

        while (true) {
            IncompleteNetwork network = pendingNetworks.peek();
            if (network == null) {
                return null;
            }

            ConduitNodeId nodeId = network.nextToBuild();
            if (nodeId != null) {
                return nodeId;
            }

            // current network is finished
            finishedNetworks.add(network);
            pendingNetworks.poll();
        }
    }

    private boolean tickIdle(int threadId) {
        int iter = 0;
        processing:
        while (true) {

            if (connectingNodes[threadId] == null) {
                ConduitNodeId nodeId;
                IncompleteNetwork network;
                computeLock.lock();
                try {
                    nodeId = takePendingNode();
                    network = pendingNetworks.peek();
                } finally {
                    computeLock.unlock();
                }

                if (nodeId == null || network == null) {
                    return false;
                }


                NodeBFSIterator bfs = new NodeBFSIterator(network::cachedNeighbors, nodeId);
                connectingNodes[threadId] = new ConnectingNode(nodeId, bfs, network);
            }

            ConnectingNode current = connectingNodes[threadId];

            IncompleteNetwork network = current.network();
            NodeBFSIterator bfs = current.bfs();

            while (bfs.hasNext()) {
                if (iter >= MAX_COMPUTE_ITER_PER_TICK) {
                    return true;
                }
                if (iter % 1000 == 0) {
                    if (network.isCancelled()) {
                        break;
                    }
                }
                current.bfs().next();
                iter++;
            }

            if (current.network().isCancelled()) {
                connectingNodes[threadId] = null;
                continue processing;
            }
            // 计算完成，保存距离
            updateDistance(current.network(), current.bfs());

            connectingNodes[threadId] = null;

        }

    }


}
