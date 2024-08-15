package dev.vfyjxf.conduitstratus.api.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.ConduitType;
import dev.vfyjxf.conduitstratus.api.conduit.event.ConduitNetworkEvent;
import dev.vfyjxf.conduitstratus.api.event.IEventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.eclipse.collections.api.list.MutableList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.NonExtendable
public interface Network extends IEventHandler<ConduitNetworkEvent> {

    /**
     * @return the center node define this network.
     */
    @Nullable
    NetworkNode getCenter();

    MutableList<? extends NetworkNode> getNodes();

    /**
     * @return true if this network has no nodes
     */
    boolean isEmpty();

    boolean support(ConduitType definition);

    boolean hasService(NetworkServiceType<?> type);

    /**
     * @param type the network service type.
     * @param <T>  the network service type.
     * @return the network service.
     * @throws NullPointerException if the service not found.
     */
    <T extends NetworkService<T>> T getService(NetworkServiceType<T> type);

    <T extends NetworkService<T>> T getOrCreateService(NetworkServiceType<T> type);

    boolean updateNetwork();

    void tick();

}
