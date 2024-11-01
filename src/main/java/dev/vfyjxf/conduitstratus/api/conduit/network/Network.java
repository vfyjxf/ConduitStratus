package dev.vfyjxf.conduitstratus.api.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.HandleType;
import dev.vfyjxf.conduitstratus.api.conduit.event.ConduitNetworkEvent;
import dev.vfyjxf.conduitstratus.api.conduit.trait.ConduitTrait;
import dev.vfyjxf.conduitstratus.api.event.EventHandler;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

@ApiStatus.NonExtendable
public interface Network extends EventHandler<ConduitNetworkEvent> {

    /**
     * @return the center node define this network.
     */
    @Nullable
    NetworkNode getCenter();

    MutableList<? extends NetworkNode> getNodes();

    int size();

    /**
     * @return true if this network has no nodes
     */
    boolean isEmpty();

    boolean hasService(NetworkServiceType<?> type);

    /**
     * @param type the network service type.
     * @param <T>  the network service type.
     * @return the network service.
     * @throws NullPointerException if the service not found.
     */
    <T extends NetworkService<T>> T getService(NetworkServiceType<T> type);

    <T extends NetworkService<T>> T getOrCreateService(NetworkServiceType<T> type);

    @Unmodifiable
    MutableMap<HandleType, ? extends NetworkChannels<?>> getChannels();

    <T extends ConduitTrait> NetworkChannels<T> getChannel(HandleType type);

    boolean updateNetwork();

    void tick();

}
