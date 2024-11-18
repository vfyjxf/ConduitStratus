package dev.vfyjxf.conduitstratus.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.HandleType;
import dev.vfyjxf.conduitstratus.api.conduit.event.NetworkEvent;
import dev.vfyjxf.conduitstratus.api.conduit.io.LogisticManager;
import dev.vfyjxf.conduitstratus.api.conduit.network.Network;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkChannels;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkService;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkServiceType;
import dev.vfyjxf.conduitstratus.api.conduit.trait.Trait;
import dev.vfyjxf.conduitstratus.api.event.EventChannel;
import dev.vfyjxf.conduitstratus.init.StratusRegistryImpl;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Predicate;

public final class ConduitNetwork implements Network {

    private static final Logger logger = LoggerFactory.getLogger("ConduitStratus-ConduitNetwork");

    private static final int NORMAL_CAPACITY = 3;

    private final EventChannel<NetworkEvent> eventChannelImpl = EventChannel.create(this);
    private final MutableMap<NetworkServiceType<?>, NetworkService<?>> services = Maps.mutable.empty();
    private final MutableList<ConduitNetworkNode> nodes = Lists.mutable.empty();
    private final MutableMap<HandleType, TypedNetworkChannels<?>> channels = Maps.mutable.withInitialCapacity(NORMAL_CAPACITY);
    private @Nullable ConduitNetworkNode center;

    @ApiStatus.Internal
    public void addNode(ConduitNetworkNode node) {
        nodes.add(node);
    }

    @ApiStatus.Internal
    public void removeNode(ConduitNetworkNode node) {
        nodes.remove(node);
    }

    @Override
    @Nullable
    public NetworkNode getCenter() {
        return center;
    }

    @ApiStatus.Internal
    public void setCenter(ConduitNetworkNode center) {
        this.center = center;
        center.setNetwork(this);
    }

    @Override
    public MutableList<? extends NetworkNode> getNodes() {
        return nodes.asUnmodifiable();
    }

    @Override
    public int size() {
        return nodes.size();
    }

    @Override
    public boolean isEmpty() {
        return nodes.isEmpty();
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
    public void tick(long currentTick) {

    }

    @Override
    public EventChannel<NetworkEvent> events() {
        return eventChannelImpl;
    }
}
