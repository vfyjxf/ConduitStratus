package dev.vfyjxf.conduitstratus.api.conduit.network;

import dev.vfyjxf.cloudlib.api.event.EventHandler;
import dev.vfyjxf.conduitstratus.api.conduit.HandleType;
import dev.vfyjxf.conduitstratus.api.conduit.connection.ConduitDistance;
import dev.vfyjxf.conduitstratus.api.conduit.connection.ConduitNodeId;
import dev.vfyjxf.conduitstratus.api.conduit.event.NetworkEvent;
import dev.vfyjxf.conduitstratus.api.conduit.trait.Trait;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.function.Predicate;

@ApiStatus.NonExtendable
public interface Network extends EventHandler<NetworkEvent>, BaseNetwork {

    MutableCollection<? extends NetworkNode> getActiveNodes();

    NetworkNode getNode(ResourceKey<Level> dimension, BlockPos pos);

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

    <TRAIT extends Trait> NetworkChannels<TRAIT> createChannels(HandleType handleType, Predicate<Trait> traitPredicate);

    <T extends Trait> NetworkChannels<T> getChannel(HandleType type);

    boolean updateNetwork();

    void tick(MinecraftServer server, long currentTick);

    void destroy();

    ConduitDistance getDistance();
}
