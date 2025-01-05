package dev.vfyjxf.conduitstratus.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.HandleType;
import dev.vfyjxf.conduitstratus.api.conduit.connection.ConduitDistance;
import dev.vfyjxf.conduitstratus.api.conduit.connection.ConduitNode;
import dev.vfyjxf.conduitstratus.api.conduit.connection.ConduitNodeId;
import dev.vfyjxf.conduitstratus.api.conduit.event.NetworkEvent;
import dev.vfyjxf.conduitstratus.api.conduit.io.LogisticManager;
import dev.vfyjxf.conduitstratus.api.conduit.network.*;
import dev.vfyjxf.conduitstratus.api.conduit.trait.Trait;
import dev.vfyjxf.conduitstratus.api.event.EventChannel;
import dev.vfyjxf.conduitstratus.blockentity.NetworkBlockEntity;
import dev.vfyjxf.conduitstratus.conduit.blockentity.ConduitBlockEntity;
import dev.vfyjxf.conduitstratus.init.StratusRegistryImpl;
import dev.vfyjxf.conduitstratus.utils.tick.TickDispatcher;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.collection.mutable.CollectionAdapter;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Unmodifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Predicate;

public final class ConduitNetwork implements Network {

    private static final Logger logger = LoggerFactory.getLogger("ConduitStratus-ConduitNetwork");

    private static final int NORMAL_CAPACITY = 3;

    private final EventChannel<NetworkEvent> eventChannelImpl = EventChannel.create(this);
    private final MutableMap<NetworkServiceType<?>, NetworkService<?>> services = Maps.mutable.empty();
    private final MutableMap<HandleType, TypedNetworkChannels<?>> channels = Maps.mutable.withInitialCapacity(NORMAL_CAPACITY);

    private final MutableMap<ConduitNodeId, ConduitNetworkNode> nodes;

    private final ConduitDistance distance;
    private final ImmutableList<ConduitNodeId> nodeIds;
    private boolean initialized = false;

    @Override
    public ConduitDistance getDistance() {
        return distance;
    }

    private ConduitNetwork(ConduitDistance distance, ImmutableList<ConduitNodeId> nodeIds) {
        this.distance = distance;
        this.nodeIds = nodeIds;
        this.nodes = Maps.mutable.withInitialCapacity(nodeIds.size());
    }


    public static ConduitNetwork create(ImmutableList<ConduitNodeId> nodeIds, ConduitDistance distance) {
        ConduitNetwork conduitNetwork = new ConduitNetwork(distance, nodeIds);
        TickDispatcher.instance().addNetwork(conduitNetwork);
        return conduitNetwork;
    }

    private void init(MinecraftServer server) {
        if (initialized) {
            return;
        }
        initialized = true;

        for (ConduitNodeId nodeId : nodeIds) {
            Level level = server.getLevel(nodeId.dimension());
            if (level == null) {
                logger.error("Level not found: {}", nodeId.dimension());
                // TODO: handle invalid nodes
                continue;
            }

            BlockPos pos = nodeId.pos();

            BlockEntity blockEntity =level.getBlockEntity(pos);

            if (!(blockEntity instanceof NetworkBlockEntity networkBlockEntity)) {
                logger.error("NetworkBlockEntity not found: {}", pos);
                continue;
            }

            var node = (ConduitNetworkNode) networkBlockEntity.getNode();

            if(node == null) {
                logger.error("NetworkNode not found: {}", pos);
                continue;
            }

            node.setNetwork(this);
            nodes.put(nodeId, node);

        }
    }

    @Override
    public MutableCollection<? extends ConduitNetworkNode> getNodes() {
        return CollectionAdapter.adapt(nodes.values());
    }

    @Override
    public ImmutableList<ConduitNodeId> getNodeIds() {
        return nodeIds;
    }

    @Override
    public NetworkNode getNode(ResourceKey<Level> dimension, BlockPos pos) {
        if(!initialized) {
            throw new IllegalStateException("Network not initialized");
        }
        return nodes.get(new ConduitNodeId(dimension, pos));
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
        init(server);
    }

    @Override
    public void destory() {
        for (ConduitNetworkNode node : nodes.values()) {
            node.onNetworkDestroy();
        }
        nodes.clear();
        services.clear();
        channels.clear();

        TickDispatcher.instance().removeNetwork(this);
    }

    @Override
    public EventChannel<NetworkEvent> events() {
        return eventChannelImpl;
    }
}
