package dev.vfyjxf.conduitstratus.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.ConduitType;
import dev.vfyjxf.conduitstratus.api.conduit.data.INetworkContext;
import dev.vfyjxf.conduitstratus.api.conduit.data.NetworkContextType;
import dev.vfyjxf.conduitstratus.api.conduit.event.IConduitNetworkEvent;
import dev.vfyjxf.conduitstratus.api.conduit.network.INetwork;
import dev.vfyjxf.conduitstratus.api.conduit.network.INetworkNode;
import dev.vfyjxf.conduitstratus.api.conduit.network.INetworkService;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkServiceType;
import dev.vfyjxf.conduitstratus.api.event.IEventChannel;
import dev.vfyjxf.conduitstratus.event.EventChannel;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public final class ConduitNetwork implements INetwork {

    private final EventChannel<IConduitNetworkEvent> eventChannel = new EventChannel<>(this);
    private final MutableMap<NetworkServiceType<?>, INetworkService> services = Maps.mutable.empty();
    private final MutableMap<NetworkContextType<?>, INetworkContext<?>> contexts = Maps.mutable.empty();
    private final MutableList<NetworkNode> nodes = Lists.mutable.empty();
    private final NetworkTickManager tickManager = new NetworkTickManager();
    private @Nullable NetworkNode center;

    @ApiStatus.Internal
    public void addNode(NetworkNode node) {
        nodes.add(node);
    }

    @ApiStatus.Internal
    public void removeNode(NetworkNode node) {
        nodes.remove(node);
    }

    @Override
    @Nullable
    public INetworkNode getCenter() {
        return center;
    }

    @ApiStatus.Internal
    public void setCenter(NetworkNode center) {
        center.setNetwork(this);
        this.center = center;
    }

    @Override
    public ImmutableList<INetworkNode> getNodes() {
        return nodes.toImmutable().collect(e -> e);
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
    public <T extends INetworkService> T getService(NetworkServiceType<T> type) {
        INetworkService service = services.get(type);
        if (service == null) {
            throw new NullPointerException("Service not found: " + type);
        }
        return (T) service;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends INetworkService> T getOrCreateService(NetworkServiceType<T> type) {
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
    public IEventChannel<IConduitNetworkEvent> channel() {
        return eventChannel;
    }
}
