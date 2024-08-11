package dev.vfyjxf.conduitstratus.api.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.ConduitColor;
import dev.vfyjxf.conduitstratus.api.conduit.IConduit;
import dev.vfyjxf.conduitstratus.api.conduit.trait.ConduitTraitType;
import dev.vfyjxf.conduitstratus.api.conduit.trait.IConduitTrait;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MutableMap;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;

@ApiStatus.NonExtendable
public interface INetworkNode {

    NodeStatus getStatus();

    IConduit getConduit();

    BlockEntity getHolder();

    default BlockPos getPos() {
        return getHolder().getBlockPos();
    }

    @Nullable
    default Level getLevel() {
        return getHolder().getLevel();
    }

    INetwork getNetwork();

    default boolean acceptsTrait(IConduitTrait<?> trait) {
        return getConduit().acceptsTrait(trait);
    }

    boolean hasTrait(ConduitTraitType<?> type);

    /**
     * @param type the trait type
     * @param <T>  the trait type
     * @return the list create the attached traits, if the type is not attached, an empty list will be returned.
     */
    <T extends IConduitTrait<T>> ImmutableMap<Direction, IConduitTrait<T>> getTraits(ConduitTraitType<T> type);

    ImmutableMap<ConduitTraitType<?>, MutableMap<Direction, IConduitTrait<?>>> allTraits();

    /**
     * @return the directions define existing connections.
     */
    RichIterable<Direction> getDirections();

    ImmutableMap<Direction, INetworkConnection> getConnectionsMap();

    @Nullable
    INetworkConnection getConnection(Direction direction);

    RichIterable<INetworkConnection> getConnections();

    @Nullable
    INetworkNode getNodeWithDirection(Direction direction);

    void rejectDirection(Direction direction);

    void removeRejection(Direction direction);

    boolean connectable(Direction direction, INetworkNode node);

    boolean connected(Direction direction);

    void disconnect(Direction direction);

    void disconnect(INetworkConnection connection);

    default void disconnectAll() {
        for (Direction value : Direction.values()) {
            disconnect(value);
        }
    }

    boolean canWorkWith(Direction direction);

    default ConduitColor getColor() {
        return getConduit().getColor();
    }

    void tick();

}
