package dev.vfyjxf.conduitstratus.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.IConduit;
import dev.vfyjxf.conduitstratus.api.conduit.network.INetwork;
import dev.vfyjxf.conduitstratus.api.conduit.network.INetworkConnection;
import dev.vfyjxf.conduitstratus.api.conduit.network.INetworkNode;
import dev.vfyjxf.conduitstratus.api.conduit.network.NodeStatus;
import dev.vfyjxf.conduitstratus.api.conduit.trait.ConduitTraitType;
import dev.vfyjxf.conduitstratus.api.conduit.trait.IConduitTrait;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.map.mutable.MapAdapter;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;

public class NetworkNode implements INetworkNode {

    private INetwork network;
    private final IConduit conduit;
    private final BlockEntity holder;
    private final MutableMap<Direction, INetworkConnection> connections = MapAdapter.adapt(new EnumMap<>(Direction.class));
    private final MutableSet<Direction> rejectDirections = Sets.mutable.empty();
    private final MutableMap<ConduitTraitType<?>, MutableMap<Direction, IConduitTrait<?>>> traits = Maps.mutable.empty();
    private NodeStatus status;

    public NetworkNode(INetwork network, IConduit conduit, BlockEntity holder) {
        this.network = network;
        this.conduit = conduit;
        this.holder = holder;
    }

    @Override
    public NodeStatus getStatus() {
        return status;
    }

    @Override
    public IConduit getConduit() {
        return conduit;
    }

    @Override
    public BlockEntity getHolder() {
        return holder;
    }

    @Override
    public INetwork getNetwork() {
        return network;
    }

    @ApiStatus.Internal
    public void setNetwork(INetwork network) {
        this.network = network;
    }

    @Override
    public boolean hasTrait(ConduitTraitType<?> type) {
        return traits.containsKey(type);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public <T extends IConduitTrait<T>> ImmutableMap<Direction, IConduitTrait<T>> getTraits(ConduitTraitType<T> type) {
        return ((ImmutableMap) traits.getIfAbsentPut(type, Maps.mutable::empty).toImmutable());
    }

    @Override
    public ImmutableMap<ConduitTraitType<?>, MutableMap<Direction, IConduitTrait<?>>> allTraits() {
        return traits.toImmutable();
    }

    @Override
    public RichIterable<Direction> getDirections() {
        return connections.keysView();
    }

    @Override
    public ImmutableMap<Direction, INetworkConnection> getConnectionsMap() {
        return connections.toImmutable();
    }

    @Nullable
    @Override
    public INetworkConnection getConnection(Direction direction) {
        return connections.get(direction);
    }

    @Override
    public RichIterable<INetworkConnection> getConnections() {
        return connections.valuesView();
    }

    @Nullable
    @Override
    public INetworkNode getNodeWithDirection(Direction direction) {
        return null;
    }

    @Override
    public void rejectDirection(Direction direction) {
        this.rejectDirections.add(direction);
    }

    @Override
    public void removeRejection(Direction direction) {
        this.rejectDirections.remove(direction);
    }

    @Override
    public boolean connectable(Direction direction, INetworkNode node) {
        BlockPos relative = getPos().relative(direction);

        return !rejectDirections.contains(direction) &&
                relative.equals(node.getPos()) &&
                conduit.connectable(node.getConduit());
    }

    @Override
    public boolean connected(Direction direction) {
        return connections.containsKey(direction);
    }

    @Override
    public void disconnect(Direction direction) {
        INetworkConnection remove = this.connections.remove(direction);
        if (remove == null) {
            throw new IllegalStateException("Trying to disconnect an unknown connection");
        }
        remove.destroy();
    }

    @Override
    public void disconnect(INetworkConnection connection) {
        Direction direction = connection.getDirection(this);
        this.connections.remove(direction);
        connection.destroy();
    }

    @Override
    public void disconnectAll() {
        for (INetworkConnection connection : connections.values()) {
            connection.destroy();
        }
    }

    @Override
    public boolean canWorkWith(Direction direction) {
        //TODO:
        return false;
    }

    @Override
    public void tick() {

    }
}
