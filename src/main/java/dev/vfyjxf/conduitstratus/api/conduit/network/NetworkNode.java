package dev.vfyjxf.conduitstratus.api.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.trait.ConduitTrait;
import dev.vfyjxf.conduitstratus.api.conduit.trait.TraitConnection;
import dev.vfyjxf.conduitstratus.api.conduit.trait.TraitType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Unmodifiable;

import javax.annotation.Nullable;

@ApiStatus.NonExtendable
//TODO:Should we allow non-conduit nodes?
public interface NetworkNode {

    Network getNetwork();

    BlockEntity getHolder();

    default BlockPos getPos() {
        return getHolder().getBlockPos();
    }

    default ServerLevel getLevel() {
        return (ServerLevel) getHolder().getLevel();
    }

    boolean hasTrait(TraitType type);

    /**
     * @param type the trait type
     * @return the list create the attached traits, if the type is not attached, an empty list will be returned.
     */
    @Unmodifiable
    MutableMap<Direction, ? extends ConduitTrait> getTraits(TraitType type);

    @Unmodifiable
    MutableList<? extends ConduitTrait> getTraits(Direction direction);

    @Unmodifiable
    MutableMap<Direction, MutableList<? extends ConduitTrait>> allTraits();

    @Nullable
    <T, C> T poxyCapability(BlockCapability<T, C> capability, @Nullable C context);

    /**
     * @return the directions define existing connections.
     */
    @Unmodifiable
    RichIterable<Direction> getDirections();

    @Unmodifiable
    MutableMap<Direction, ? extends NodeConnection> getConnectionsMap();

    @Nullable
    NodeConnection getConnection(Direction direction);

    @Unmodifiable
    RichIterable<? extends NodeConnection> getConnections();

    @Nullable
    NetworkNode getNodeByDirection(Direction direction);

    void rejectDirection(Direction direction);

    @ApiStatus.Internal
    boolean containsRejection(Direction direction);

    void removeRejection(Direction direction);

    boolean connectable(Direction direction, NetworkNode node);

    boolean connected(Direction direction);

    boolean connected(NetworkNode node);

    void disconnect(Direction direction);

    void disconnect(NodeConnection connection);

    default void disconnectAll() {
        for (Direction value : Direction.values()) {
            disconnect(value);
        }
    }

    /**
     * @param direction the direction to check
     * @return Whether the node can connect to the given direction with a {@link TraitConnection}
     */
    boolean canWorkWith(Direction direction);

    void tick();

}
