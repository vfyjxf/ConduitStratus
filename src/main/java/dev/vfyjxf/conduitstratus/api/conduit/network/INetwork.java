package dev.vfyjxf.conduitstratus.api.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.IConduitDefinition;
import dev.vfyjxf.conduitstratus.api.conduit.event.IConduitNetworkEvent;
import dev.vfyjxf.conduitstratus.api.event.IEventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.eclipse.collections.api.list.ImmutableList;
import org.jetbrains.annotations.Nullable;

public interface INetwork extends Iterable<INetworkNode>, IEventHandler<IConduitNetworkEvent> {

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

    boolean support(IConduitDefinition definition);

    boolean hasTrait(NetworkTraitType<?> type);

    @Nullable
    <T extends INetworkTrait> T getTrait(NetworkTraitType<T> type);

    <T extends INetworkTrait> T getOrCreateTrait(NetworkTraitType<T> type);

    boolean updateNetwork();

    void tick();

    default boolean loaded(Level level, BlockPos pos) {
        return level.isLoaded(pos);
    }

}
