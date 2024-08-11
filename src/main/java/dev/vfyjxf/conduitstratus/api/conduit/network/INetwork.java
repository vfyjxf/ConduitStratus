package dev.vfyjxf.conduitstratus.api.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.ConduitType;
import dev.vfyjxf.conduitstratus.api.conduit.event.IConduitNetworkEvent;
import dev.vfyjxf.conduitstratus.api.event.IEventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.eclipse.collections.api.list.ImmutableList;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.NonExtendable
public interface INetwork extends IEventHandler<IConduitNetworkEvent> {

    /**
     * @return the center node define this network.
     */
    @Nullable
    INetworkNode getCenter();

    ImmutableList<INetworkNode> getNodes();

    /**
     * @return true if this network has no nodes
     */
    boolean isEmpty();

    boolean support(ConduitType definition);

    boolean hasService(NetworkServiceType<?> type);

    /**
     * @param type the network service type.
     * @return the network service.
     * @param <T> the network service type.
     * @throws NullPointerException if the service not found.
     */
    <T extends INetworkService> T getService(NetworkServiceType<T> type);

    <T extends INetworkService> T getOrCreateService(NetworkServiceType<T> type);

    boolean updateNetwork();

    void tick();

    default boolean loaded(Level level, BlockPos pos) {
        return level.isLoaded(pos);
    }

}
