package dev.vfyjxf.conduitstratus.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.Conduit;
import dev.vfyjxf.conduitstratus.api.conduit.network.Network;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkConnection;
import dev.vfyjxf.conduitstratus.api.conduit.network.NodeStatus;
import dev.vfyjxf.conduitstratus.api.conduit.trait.ConduitTraitType;
import dev.vfyjxf.conduitstratus.api.conduit.trait.ConduitTrait;
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

public class ConduitNetworkNode implements dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode {

    private Network network;
    private final Conduit conduit;
    private final BlockEntity holder;
    private final MutableMap<Direction, NetworkConnection> connections = MapAdapter.adapt(new EnumMap<>(Direction.class));
    private final MutableSet<Direction> rejectDirections = Sets.mutable.empty();
    private final MutableMap<ConduitTraitType<?>, MutableMap<Direction, ConduitTrait<?>>> traits = Maps.mutable.empty();
    private NodeStatus status;

    public ConduitNetworkNode(Network network, Conduit conduit, BlockEntity holder) {
        this.network = network;
        this.conduit = conduit;
        this.holder = holder;
    }

    @Override
    public NodeStatus getStatus() {
        return status;
    }

    @Override
    public Conduit getConduit() {
        return conduit;
    }

    @Override
    public BlockEntity getHolder() {
        return holder;
    }

    @Override
    public Network getNetwork() {
        return network;
    }

    @ApiStatus.Internal
    public void setNetwork(Network network) {
        this.network = network;
    }

    @Override
    public boolean hasTrait(ConduitTraitType<?> type) {
        return traits.containsKey(type);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public <T extends ConduitTrait<T>> ImmutableMap<Direction, ConduitTrait<T>> getTraits(ConduitTraitType<T> type) {
        return ((ImmutableMap) traits.getIfAbsentPut(type, Maps.mutable::empty).toImmutable());
    }

    @Override
    public ImmutableMap<ConduitTraitType<?>, MutableMap<Direction, ConduitTrait<?>>> allTraits() {
        return traits.toImmutable();
    }

    @Override
    public RichIterable<Direction> getDirections() {
        return connections.keysView();
    }

    @Override
    public ImmutableMap<Direction, NetworkConnection> getConnectionsMap() {
        return connections.toImmutable();
    }

    @Nullable
    @Override
    public NetworkConnection getConnection(Direction direction) {
        return connections.get(direction);
    }

    @Override
    public RichIterable<NetworkConnection> getConnections() {
        return connections.valuesView();
    }

    @Nullable
    @Override
    public dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode getNodeWithDirection(Direction direction) {
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
    public boolean connectable(Direction direction, dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode node) {
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
        NetworkConnection remove = this.connections.remove(direction);
        if (remove == null) {
            throw new IllegalStateException("Trying to disconnect an unknown connection");
        }
        remove.destroy();
    }

    @Override
    public void disconnect(NetworkConnection connection) {
        Direction direction = connection.getDirection(this);
        this.connections.remove(direction);
        connection.destroy();
    }

    @Override
    public void disconnectAll() {
        for (NetworkConnection connection : connections.values()) {
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
