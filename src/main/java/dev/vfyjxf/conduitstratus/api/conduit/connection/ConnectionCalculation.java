package dev.vfyjxf.conduitstratus.api.conduit.connection;

import dev.vfyjxf.conduitstratus.init.values.ModValues;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class ConnectionCalculation {

    private record ConnectingNode(ConduitNodeId id, NodeBFSIterator bfs, IncompleteNetwork network) {
    }

    private boolean chainLoading = false;

    // 正在进行的计算
    private final ReentrantLock computeLock = new ReentrantLock();
    private final ArrayDeque<IncompleteNetwork> pendingNetworks = new ArrayDeque<>();
    private final List<IncompleteNetwork> finishedNetworks = new ArrayList<>();

    // 用于连接计算的迭代器
    private final ConnectingNode[] connectingNodes;
    private NodeBFSIterator networkBFS = null;
    private MinecraftServer server = null;


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
        tickNetwork();
        tickConnect();
        finishNetworks();
    }


    public void addIdleNode(ConduitNode node) {

        idleNodes.put(node.conduitId(), node);
    }


    private static final int MAX_ITER_PER_TICK = 100;
    private static final int MAX_CONNECTION_PARALLEL = 1;

    public ConnectionCalculation() {
        // 单线程先
        connectingNodes = new ConnectingNode[MAX_CONNECTION_PARALLEL];

    }


    private void prepareNetwork(IncompleteNetwork network, NodeBFSIterator bfs) {
        network.prepare();

        updateDistance(network, bfs);

        computeLock.lock();
        try {
            pendingNetworks.add(network);
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
        ConduitNode node = searchNode(nodeId);
        if (node == null || !node.valid()) {
            return null;
        }
        List<ConduitNodeId> neighbors = node.neighbors();

        network.addNode(new CachedNode(nodeId, neighbors));
        return neighbors;
    }

    public void tickConnect() {
        // TODO: 多线程
        for (int i = 0; i < MAX_CONNECTION_PARALLEL; i++) {
            tickIdle(i);
        }
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
            network.build();
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


    public void tickNetwork() {
        if (server == null) {
            return;
        }

        ConduitNodeId nodeId = takeIdleNode();

        // TODO: 多个网络同时计算
        if (nodeId == null) {
            return;
        }

        // find next network

        IncompleteNetwork network = new IncompleteNetwork();

        NodeBFSIterator bfs = new NodeBFSIterator((id) -> this.searchNeighbors(network, id), nodeId);
        while (bfs.hasNext()) {
            bfs.next();
        }

        for (NodeBFSIterator.IterNode iterNode : bfs.getNodes()) {
            idleNodes.remove(iterNode.nodeId);
        }

        if (bfs.hasInvalid()) {
            HashSet<ConduitNodeId> invalidNodes = new HashSet<>();
            invalidNodes.add(bfs.getNodes().getFirst().nodeId);
            for (NodeBFSIterator.IterNode iterNode : bfs.getNodes()) {
                if (iterNode.invalid) {
                    invalidNodes.add(iterNode.nodeId);
                    if (iterNode.fromId != null) {
                        invalidNodes.add(iterNode.fromId);
                    }
                }
            }

            for (ConduitNodeId invalidNode : invalidNodes) {
                ConduitNode node = searchNode(invalidNode);
                if (node != null) {
                    node.setValid(false);
                }
            }

            return;
        }

        prepareNetwork(network, bfs);


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

    private void tickIdle(int threadId) {
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
                    return;
                }


                NodeBFSIterator bfs = new NodeBFSIterator(network::cachedNeighbors, nodeId);
                connectingNodes[threadId] = new ConnectingNode(nodeId, bfs, network);
            }

            ConnectingNode current = connectingNodes[threadId];


            while (current.bfs().hasNext()) {
                if (iter >= MAX_ITER_PER_TICK) {
                    return;
                }
                current.bfs().next();
                iter++;
            }


            // 计算完成，保存距离
            updateDistance(current.network(), current.bfs());

            connectingNodes[threadId] = null;

        }

    }


}
