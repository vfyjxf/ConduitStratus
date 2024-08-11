package dev.vfyjxf.conduitstratus.api.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.ConduitColor;
import dev.vfyjxf.conduitstratus.api.conduit.Conduit;
import dev.vfyjxf.conduitstratus.api.conduit.trait.ConduitTraitType;
import dev.vfyjxf.conduitstratus.api.conduit.trait.ConduitTrait;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MutableMap;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;

@ApiStatus.NonExtendable
public interface NetworkNode {

    /**
     * TODO: decide if this is necessary
     */
    NodeStatus getStatus();

    Conduit getConduit();

    BlockEntity getHolder();

    default BlockPos getPos() {
        return getHolder().getBlockPos();
    }

    @Nullable
    default Level getLevel() {
        return getHolder().getLevel();
    }

    Network getNetwork();

    default boolean acceptsTrait(ConduitTrait<?> trait) {
        return getConduit().acceptsTrait(trait);
    }

    boolean hasTrait(ConduitTraitType<?> type);

    /**
     * @param type the trait type
     * @param <T>  the trait type
     * @return the list create the attached traits, if the type is not attached, an empty list will be returned.
     */
    <T extends ConduitTrait<T>> ImmutableMap<Direction, ConduitTrait<T>> getTraits(ConduitTraitType<T> type);

    ImmutableMap<ConduitTraitType<?>, MutableMap<Direction, ConduitTrait<?>>> allTraits();

    /**
     * @return the directions define existing connections.
     */
    RichIterable<Direction> getDirections();

    ImmutableMap<Direction, NetworkConnection> getConnectionsMap();

    @Nullable
    NetworkConnection getConnection(Direction direction);

    RichIterable<NetworkConnection> getConnections();

    @Nullable
    NetworkNode getNodeWithDirection(Direction direction);

    void rejectDirection(Direction direction);

    void removeRejection(Direction direction);

    boolean connectable(Direction direction, NetworkNode node);

    boolean connected(Direction direction);

    void disconnect(Direction direction);

    void disconnect(NetworkConnection connection);

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
