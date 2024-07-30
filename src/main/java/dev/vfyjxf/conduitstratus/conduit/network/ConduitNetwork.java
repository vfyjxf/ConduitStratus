package dev.vfyjxf.conduitstratus.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.IConduitDefinition;
import dev.vfyjxf.conduitstratus.api.conduit.event.IConduitNetworkEvent;
import dev.vfyjxf.conduitstratus.api.conduit.network.INetwork;
import dev.vfyjxf.conduitstratus.api.conduit.network.INetworkNode;
import dev.vfyjxf.conduitstratus.api.conduit.network.INetworkTrait;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkTraitType;
import dev.vfyjxf.conduitstratus.api.event.IEventChannel;
import dev.vfyjxf.conduitstratus.event.EventChannel;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public class ConduitNetwork implements INetwork {

    private final EventChannel<IConduitNetworkEvent> eventChannel = new EventChannel<>(this);
    private final MutableMap<NetworkTraitType<?>, INetworkTrait> traits = Maps.mutable.empty();
    private final MutableList<INetworkNode> nodes = Lists.mutable.empty();
    private final NetworkTickManager tickManager = new NetworkTickManager();
    @Nullable
    private INetworkNode center;

    @Override
    @Nullable
    public INetworkNode getCenter() {
        return center;
    }

    @ApiStatus.Internal
    public void setCenter(INetworkNode center) {
        center.setNetwork(this);
        this.center = center;
    }

    @Override
    public ImmutableList<INetworkNode> getNodes() {
        return nodes.toImmutable();
    }

    @Override
    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    @Override
    public boolean support(IConduitDefinition definition) {
        //TODO:conflict system
        return false;
    }

    @Override
    public boolean hasTrait(NetworkTraitType<?> type) {
        return traits.containsKey(type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends INetworkTrait> @Nullable T getTrait(NetworkTraitType<T> type) {
        return (T) traits.get(type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends INetworkTrait> T getOrCreateTrait(NetworkTraitType<T> type) {
        return (T) traits.getIfAbsentPut(type, () -> type.factory().apply(this));
    }


    @Override
    public boolean updateNetwork() {
        return false;
    }

    @Override
    public void tick() {
    }

    @NotNull
    @Override
    public Iterator<INetworkNode> iterator() {
        return nodes.iterator();
    }

    @Override
    public IEventChannel<IConduitNetworkEvent> channel() {
        return eventChannel;
    }
}
