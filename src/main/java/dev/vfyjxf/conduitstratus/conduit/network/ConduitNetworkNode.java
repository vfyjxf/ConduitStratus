package dev.vfyjxf.conduitstratus.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.network.Network;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import dev.vfyjxf.conduitstratus.api.conduit.network.NodeConnection;
import dev.vfyjxf.conduitstratus.api.conduit.trait.ConduitTrait;
import dev.vfyjxf.conduitstratus.api.conduit.trait.PoxyTrait;
import dev.vfyjxf.conduitstratus.api.conduit.trait.TraitType;
import dev.vfyjxf.conduitstratus.utils.Checks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.map.mutable.MapAdapter;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;

@ApiStatus.Internal
public class ConduitNetworkNode implements NetworkNode, NetworkHolder {

    private ConduitNetwork network;
    private final ServerLevel level;
    private final BlockEntity holder;
    private final MutableMap<Direction, ConduitNodeConnection> connections = MapAdapter.adapt(new EnumMap<>(Direction.class));
    private final EnumSet<Direction> rejectDirections = EnumSet.noneOf(Direction.class);
    private final MutableMap<Direction, MutableList<? extends ConduitTrait>> traits = Maps.mutable.empty();

    public ConduitNetworkNode(BlockEntity holder) {
        Checks.checkArgument(holder.getLevel() instanceof ServerLevel, "The given BlockEntity must be in a ServerLevel");
        this.holder = holder;
        this.level = (ServerLevel) holder.getLevel();
    }

    @Override
    public BlockEntity getHolder() {
        return holder;
    }

    @Override
    public ConduitNetwork getNetwork() {
        if (network == null) {
            throw new IllegalStateException("Network is not initialized yet or it was destroyed");
        }
        return network;
    }

    @Nullable
    public ConduitNetwork getNetworkUnsafe() {
        return network;
    }

    @Override
    @ApiStatus.Internal
    public void setNetwork(Network network) {
        this.network = (ConduitNetwork) network;
        this.network.addNode(this);
    }

    @Override
    public ServerLevel getLevel() {
        return level;
    }

    @Override
    public boolean hasTrait(TraitType type) {
        return traits.anySatisfy(traits -> traits.anySatisfy(trait -> trait.getType() == type));
    }

    @Override
    public MutableMap<Direction, ? extends ConduitTrait> getTraits(TraitType type) {
        return traits.collectValues((direction, traits) -> traits.detect(trait -> trait.getType() == type));
    }

    @Override
    public @Unmodifiable MutableList<? extends ConduitTrait> getTraits(Direction direction) {
        return traits.get(direction).asUnmodifiable();
    }

    @Override
    public @Unmodifiable MutableMap<Direction, MutableList<? extends ConduitTrait>> allTraits() {
        return traits.asUnmodifiable();
    }

    @Override
    public @Nullable <T, C> T poxyCapability(BlockCapability<T, C> capability, @Nullable C context) {
        if (context instanceof Direction direction) {
            T cap = poxyCapability(traits.get(direction), capability);
            if (cap != null) return cap;
        }
        if (context == null) {
            for (MutableList<? extends ConduitTrait> traits : traits) {
                T cap = poxyCapability(traits, capability);
                if (cap != null) return cap;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <T> @Nullable T poxyCapability(MutableList<? extends ConduitTrait> traits, BlockCapability<T, ?> capability) {
        for (ConduitTrait trait : traits) {
            if (trait instanceof PoxyTrait<?> poxyTrait && poxyTrait.getPoxyToken() == capability) {
                var cap = poxyTrait.poxyCapability();
                if (cap != null) return (T) cap;
            }
        }
        return null;
    }

    public void addConnection(Direction direction, ConduitNodeConnection connection) {
        if (connections.containsKey(direction)) {
            throw new IllegalArgumentException("Trying to add a connection to an already connected direction: " + direction);
        }
        connections.put(direction, connection);
    }

    @Override
    public @Unmodifiable RichIterable<Direction> getDirections() {
        return connections.keysView();
    }

    @Override
    public MutableMap<Direction, ? extends NodeConnection> getConnectionsMap() {
        return connections.asUnmodifiable();
    }

    @Nullable
    @Override
    public NodeConnection getConnection(Direction direction) {
        return connections.get(direction);
    }

    @Override
    public @Unmodifiable RichIterable<? extends NodeConnection> getConnections() {
        return connections.valuesView();
    }

    @Nullable
    @Override
    public NetworkNode getNodeByDirection(Direction direction) {
        NodeConnection connection = connections.get(direction);
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
                !rejectDirections.contains(direction) &&
                !node.containsRejection(direction.getOpposite()) &&
                relative.equals(node.getPos());
    }

    @Override
    public boolean connected(Direction direction) {
        return connections.containsKey(direction);
    }

    @Override
    public boolean connected(NetworkNode node) {
        if (node == this) return false;
        for (var connection : connections) {
            if (connection.left() == node || connection.right() == node) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void disconnect(Direction direction) {
        NodeConnection remove = this.connections.remove(direction);
        if (remove == null) {
            throw new IllegalArgumentException("Trying to disconnect an unknown connection from: " + this + " at: " + direction);
        }
        remove.destroy();
    }

    @Override
    public void disconnect(NodeConnection connection) {
        Direction direction = connection.getDirection(this);
        this.connections.remove(direction);
        connection.destroy();
    }

    @Override
    public void disconnectAll() {
        for (Iterator<ConduitNodeConnection> iterator = connections.values().iterator(); iterator.hasNext(); ) {
            NodeConnection connection = iterator.next();
            iterator.remove();
            connection.destroy();
        }
    }

    public void destroy() {
        disconnectAll();
    }

    @Override
    public boolean canWorkWith(Direction direction) {
        //TODO:
        return false;
    }

    @Override
    public void tick() {

    }

    @ApiStatus.Internal
    public void saveData(CompoundTag data) {

    }

    @ApiStatus.Internal
    public void loadData(CompoundTag data) {

    }

    @ApiStatus.Internal
    public void onReady() {

    }

    @Override
    public String toString() {
        return "ConduitNetworkNode{" +
                " pos=" + getPos() +
                '}';
    }
}
