package dev.vfyjxf.conduitstratus.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.ConduitType;
import dev.vfyjxf.conduitstratus.api.conduit.event.ConduitNetworkEvent;
import dev.vfyjxf.conduitstratus.api.conduit.network.Network;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkService;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkServiceType;
import dev.vfyjxf.conduitstratus.api.event.IEventChannel;
import dev.vfyjxf.conduitstratus.event.EventChannel;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public final class ConduitNetwork implements Network {

    private final EventChannel<ConduitNetworkEvent> eventChannel = new EventChannel<>(this);
    private final MutableMap<NetworkServiceType<?>, NetworkService> services = Maps.mutable.empty();
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
        return nodes.clone();
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
        NetworkService<T> service = services.get(type);
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
    public IEventChannel<ConduitNetworkEvent> events() {
        return eventChannel;
    }
}
