package dev.vfyjxf.conduitstratus.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.ConduitType;
import dev.vfyjxf.conduitstratus.api.conduit.event.ConduitNetworkEvent;
import dev.vfyjxf.conduitstratus.api.conduit.network.Network;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkService;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkServiceType;
import dev.vfyjxf.conduitstratus.api.event.EventChannel;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ConduitNetwork implements Network {

    public static final Logger logger = LoggerFactory.getLogger("ConduitStratus-ConduitNetwork");

    private final EventChannel<ConduitNetworkEvent> eventChannelImpl = EventChannel.create(this);
    private final MutableMap<NetworkServiceType<?>, NetworkService<?>> services = Maps.mutable.empty();
    private final MutableList<ConduitNetworkNode> nodes = Lists.mutable.empty();
    private final NetworkTickManager tickManager = new NetworkTickManager();
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
        center.setNetwork(this);
        this.center = center;
    }

    @Override
    public MutableList<? extends NetworkNode> getNodes() {
        return nodes.asUnmodifiable();
    }

    @Override
    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    @Override
    public boolean support(ConduitType definition) {
        //TODO:conflict system
        return true;
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
    public boolean updateNetwork() {
        return false;
    }

    @Override
    public void tick() {
    }

    @Override
    public EventChannel<ConduitNetworkEvent> events() {
        return eventChannelImpl;
    }
}
