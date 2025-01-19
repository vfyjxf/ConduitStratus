package dev.vfyjxf.conduitstratus.conduit.network;

import dev.vfyjxf.cloudlib.api.event.EventChannel;
import dev.vfyjxf.conduitstratus.api.conduit.HandleType;
import dev.vfyjxf.conduitstratus.api.conduit.connection.ConduitDistance;
import dev.vfyjxf.conduitstratus.api.conduit.connection.ConduitNode;
import dev.vfyjxf.conduitstratus.api.conduit.connection.ConduitNodeId;
import dev.vfyjxf.conduitstratus.api.conduit.event.NetworkEvent;
import dev.vfyjxf.conduitstratus.api.conduit.io.LogisticManager;
import dev.vfyjxf.conduitstratus.api.conduit.network.*;
import dev.vfyjxf.conduitstratus.api.conduit.trait.Trait;
import dev.vfyjxf.conduitstratus.init.StratusRegistryImpl;
import dev.vfyjxf.conduitstratus.init.values.ModValues;
import dev.vfyjxf.conduitstratus.utils.tick.TickDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.collection.mutable.CollectionAdapter;
import org.jetbrains.annotations.Unmodifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public final class ConduitNetwork implements Network {

    private static final Logger logger = LoggerFactory.getLogger("ConduitStratus-ConduitNetwork");

    private static final int NORMAL_CAPACITY = 3;

    private final EventChannel<NetworkEvent> eventChannelImpl = EventChannel.create(this);
    private final MutableMap<NetworkServiceType<?>, NetworkService<?>> services = Maps.mutable.empty();
    private final MutableMap<HandleType, TypedNetworkChannels<?>> channels = Maps.mutable.withInitialCapacity(NORMAL_CAPACITY);

    private final MutableMap<ConduitNodeId, ConduitNetworkNode> activeNodes;

    private ConduitDistance distance;
    private List<ConduitNodeId> nodeIds;
    private final UUID uuid;

    private boolean destroyed = false;

    @Override
    public ConduitDistance getDistance() {
        return distance;
    }

    private ConduitNetwork(UUID uuid, ConduitDistance distance, List<ConduitNodeId> nodeIds) {
        this.distance = distance;
        this.nodeIds = nodeIds;
        this.activeNodes = Maps.mutable.withInitialCapacity(nodeIds.size());
        this.uuid = uuid;
    }


    public static ConduitNetwork create(UUID uuid, MinecraftServer server, List<ConduitNodeId> nodeIds, ConduitDistance distance) {
        ConduitNetwork conduitNetwork = new ConduitNetwork(uuid, distance, nodeIds);
        TickDispatcher.instance().addNetwork(conduitNetwork);
        conduitNetwork.init(server);
        return conduitNetwork;
    }

    private void init(MinecraftServer server) {

        for (ConduitNodeId nodeId : nodeIds) {
            Level level = server.getLevel(nodeId.dimension());
            if (level == null) {
                logger.error("Level not found: {}", nodeId.dimension());
                // TODO: handle invalid nodes
                continue;
            }


            ConduitNode conduitNode = level.getCapability(ModValues.CONDUIT_NODE_CAP, nodeId.pos());
            if (conduitNode == null) {
                logger.error("ConduitNode not found: {}", nodeId);
                continue;
            }

            conduitNode.setNetwork(this);

//            BlockEntity blockEntity = level.getBlockEntity(pos);
//
//            if (!(blockEntity instanceof NetworkBlockEntity networkBlockEntity)) {
//                logger.error("NetworkBlockEntity not found: {}", pos);
//                continue;
//            }
//
//            var node = (ConduitNetworkNode) networkBlockEntity.getNode();
//
//            if (node == null) {
//                logger.error("NetworkNode not found: {}", pos);
//                continue;
//            }
//
//            node.setNetwork(this);
//            activeNodes.put(nodeId, node);

        }
    }

    @Override
    public MutableCollection<? extends ConduitNetworkNode> getActiveNodes() {
        return CollectionAdapter.adapt(activeNodes.values());
    }


    @Override
    public NetworkNode getNode(ResourceKey<Level> dimension, BlockPos pos) {
        return activeNodes.get(new ConduitNodeId(dimension, pos));
    }

    @Override
    public int size() {
        return nodeIds.size();
    }

    @Override
    public boolean isEmpty() {
        return nodeIds.isEmpty();
    }

    @Override
    public boolean hasService(NetworkServiceType<?> type) {
        return services.containsKey(type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends NetworkService<T>> T getService(NetworkServiceType<T> type) {
        NetworkService<T> service = (NetworkService<T>) services.get(type);
        if (service == null) {
            throw new NullPointerException("Service not found: " + type);
        }
        return (T) service;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends NetworkService<T>> T getOrCreateService(NetworkServiceType<T> type) {
        return (T) services.getIfAbsentPut(type, () -> type.factory().apply(this));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <TRAIT extends Trait> NetworkChannels<TRAIT> createChannels(HandleType handleType, Predicate<Trait> traitPredicate) {
        TypedNetworkChannels<?> channels = this.channels.get(handleType);
        if (channels == null) {
            channels = new TypedNetworkChannels<>(this, handleType, traitPredicate);
            this.channels.put(handleType, channels);
        }
        return (NetworkChannels<TRAIT>) channels;
    }

    @Override
    public @Unmodifiable MutableMap<HandleType, ? extends NetworkChannels<?>> getChannels() {
        return channels.asUnmodifiable();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Trait> NetworkChannels<T> getChannel(HandleType type) {
        return (NetworkChannels<T>) channels.getIfAbsentPut(type, () -> {
            LogisticManager<?, ?, ?> logisticManager = StratusRegistryImpl.INSTANCE.getLogisticManager(type);
            if (logisticManager == null) {
                throw new NullPointerException("LogisticManager for : " + type + " not found.");
            }
            return (TypedNetworkChannels<?>) logisticManager.createChannels(this);
        });
    }

    @Override
    public boolean updateNetwork() {
        return false;
    }

    @Override
    public void tick(MinecraftServer server, long currentTick) {

    }

    @Override
    public UUID uuid() {
        return uuid;
    }

    @Override
    public void destroy() {
        for (ConduitNetworkNode node : activeNodes.values()) {
            node.onNetworkDestroy();
        }
        destroyed = true;
        activeNodes.clear();
        services.clear();
        channels.clear();
        nodeIds = null;
        distance = null;

        TickDispatcher.instance().removeNetwork(this);
    }

    @Override
    public List<ConduitNodeId> nodeIds() {
        return nodeIds;
    }

    @Override
    public NetworkStatus status() {
        if (destroyed) {
            return NetworkStatus.Destroyed;
        }
        return NetworkStatus.Ready;
    }

    @Override
    public EventChannel<NetworkEvent> events() {
        return eventChannelImpl;
    }
}
