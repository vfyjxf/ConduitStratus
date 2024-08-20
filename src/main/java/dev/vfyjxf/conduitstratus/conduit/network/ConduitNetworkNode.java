package dev.vfyjxf.conduitstratus.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.Conduit;
import dev.vfyjxf.conduitstratus.api.conduit.network.Network;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkConnection;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import dev.vfyjxf.conduitstratus.api.conduit.network.NodeStatus;
import dev.vfyjxf.conduitstratus.api.conduit.trait.ConduitTrait;
import dev.vfyjxf.conduitstratus.api.conduit.trait.ConduitTraitType;
import dev.vfyjxf.conduitstratus.utils.Checks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.map.mutable.MapAdapter;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.EnumMap;
import java.util.Iterator;

public class ConduitNetworkNode implements NetworkNode, ConduitNetworkHolder {

    private Network network;
    private final ServerLevel level;
    private final Conduit conduit;
    private final BlockEntity holder;
    private final MutableMap<Direction, NetworkConnection> connections = MapAdapter.adapt(new EnumMap<>(Direction.class));
    private final MutableSet<Direction> rejectDirections = Sets.mutable.empty();
    private final MutableMap<ConduitTraitType<?>, MutableMap<Direction, ? extends ConduitTrait<?>>> traits = Maps.mutable.empty();
    private NodeStatus status = NodeStatus.ACTIVE;

    public ConduitNetworkNode(Network network, Conduit conduit, BlockEntity holder) {
        Checks.checkArgument(holder.getLevel() instanceof ServerLevel, "The given BlockEntity must be in a ServerLevel");
        this.network = network;
        this.conduit = conduit;
        this.holder = holder;
        this.level = (ServerLevel) holder.getLevel();
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
    public ServerLevel getLevel() {
        return level;
    }

    @Override
    public boolean hasTrait(ConduitTraitType<?> type) {
        return traits.containsKey(type);
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public <T extends ConduitTrait<T>> MutableMap<Direction, ? extends ConduitTrait<T>> getTraits(ConduitTraitType<T> type) {
        return (MutableMap<Direction, ? extends ConduitTrait<T>>) traits.getIfAbsentPut(type, Maps.mutable::empty);
    }

    @Override
    public MutableMap<ConduitTraitType<?>, MutableMap<Direction, ? extends ConduitTrait<?>>> allTraits() {
        return traits.clone();
    }

    @Override
    public @Unmodifiable RichIterable<Direction> getDirections() {
        return connections.keysView();
    }

    @Override
    public MutableMap<Direction, NetworkConnection> getConnectionsMap() {
        return connections.clone();
    }

    @Nullable
    @Override
    public NetworkConnection getConnection(Direction direction) {
        return connections.get(direction);
    }

    @Override
    public @Unmodifiable RichIterable<NetworkConnection> getConnections() {
        return connections.valuesView();
    }

    @Nullable
    @Override
    public NetworkNode getNodeByDirection(Direction direction) {
        NetworkConnection connection = connections.get(direction);
        if (connection == null) return null;

        return connection.getOtherSide(this);
    }

    @Override
    public void rejectDirection(Direction direction) {
        this.rejectDirections.add(direction);
    }

    @Override
    public boolean containsRejection(Direction direction) {
        return this.rejectDirections.contains(direction);
    }

    @Override
    public void removeRejection(Direction direction) {
        this.rejectDirections.remove(direction);
    }

    @Override
    public boolean connectable(Direction direction, NetworkNode node) {
        BlockPos relative = getPos().relative(direction);

        return this.network == node.getNetwork() &&
                getConduit().connectable(node.getConduit()) &&
                !rejectDirections.contains(direction) &&
                !node.containsRejection(direction.getOpposite()) &&
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
            throw new IllegalArgumentException("Trying to disconnect an unknown connection from: " + this + " at: " + direction);
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
        for (Iterator<NetworkConnection> iterator = connections.values().iterator(); iterator.hasNext(); ) {
            NetworkConnection connection = iterator.next();
            iterator.remove();
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

    @Override
    public String toString() {
        return "ConduitNetworkNode{" +
                " pos=" + getPos() +
                '}';
    }
}
