package dev.vfyjxf.conduitstratus.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.network.Network;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkBuilder;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkChannels;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNodeVisitor;
import dev.vfyjxf.conduitstratus.api.conduit.network.NodeConnection;
import dev.vfyjxf.conduitstratus.api.conduit.trait.PoxyTrait;
import dev.vfyjxf.conduitstratus.api.conduit.trait.Trait;
import dev.vfyjxf.conduitstratus.api.conduit.trait.TraitType;
import dev.vfyjxf.conduitstratus.conduit.blockentity.ConduitBlockEntity;
import dev.vfyjxf.conduitstratus.utils.Checks;
import dev.vfyjxf.conduitstratus.utils.EnumConstant;
import dev.vfyjxf.conduitstratus.utils.LevelHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.map.mutable.MapAdapter;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Objects;

@ApiStatus.Internal
public class ConduitNetworkNode implements NetworkNode, NetworkHolder {

    private static final int TRAIT_CAPACITY = 3;

    private ConduitNetwork network;
    //TODO:Decide identifier format
    private String identifier;
    private final ServerLevel level;
    private final BlockEntity holder;
    private final MutableMap<Direction, ConduitNodeConnection> connections = MapAdapter.adapt(new EnumMap<>(Direction.class));
    private final EnumSet<Direction> rejectDirections = EnumSet.noneOf(Direction.class);
    private final MutableMap<Direction, MutableList<Trait>> traits = Maps.mutable.empty();

    private boolean initialized = false;
    private Object visited = null;

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
    public String identifier() {
        return identifier;
    }

    @Override
    public ConduitNetwork getNetwork() {
        if (network == null) {
            throw new IllegalStateException("Network is not initialized yet or it was destroyed");
        }
        return network;
    }

    private void updateNode() {
        if (!initialized) return;
        if (network == null) {
            this.setNetwork(ConduitNetwork.create());
        }
    }

    private void updateConnections() {
        if (!initialized) return;
        for (Direction direction : EnumConstant.directions) {
            if (rejectDirections.contains(direction)) continue;
            BlockPos pos = getPos().relative(direction);
            if (this.connected(direction) || !this.getTraits(direction).isEmpty()) continue;
            BlockEntity blockEntity = LevelHelper.getBlockEntity(level, pos);
            if (blockEntity instanceof ConduitBlockEntity conduitBlockEntity) {
                NetworkNode node = conduitBlockEntity.getNetworkNode().getNode();
                //todo:check conduit connectable
                Direction opposite = direction.getOpposite();
                if (node != null && !node.connected(opposite) && node.getTraits(opposite).isEmpty()) {
                    NetworkBuilder.createConnection(this, node, direction);
                }
            }
        }
    }

    @Nullable
    public ConduitNetwork getNetworkUnsafe() {
        return network;
    }

    @Override
    @ApiStatus.Internal
    public void setNetwork(Network network) {
        if (this.network == network) return;
        if (this.network != null) {
            this.network.removeNode(this);
        }
        this.network = (ConduitNetwork) network;
        this.network.addNode(this);
    }

    @Override
    public ServerLevel getLevel() {
        return level;
    }

    @Override
    public boolean positive() {
        return false;
    }

    @Override
    public void addTrait(Direction direction, Trait trait) {
        traits.getIfAbsentPut(direction, Lists.mutable.withInitialCapacity(TRAIT_CAPACITY)).add(trait);
        NetworkChannels<Trait> channel = network.getChannel(trait.getHandleType());
        channel.addTrait(trait);
    }

    @Override
    public boolean hasTrait(TraitType type) {
        return traits.anySatisfy(traits -> traits.anySatisfy(trait -> trait.getType() == type));
    }

    @Override
    public MutableMap<Direction, ? extends Trait> getTraits(TraitType type) {
        return traits.collectValues((direction, traits) -> traits.detect(trait -> trait.getType() == type));
    }

    @Override
    public @Unmodifiable MutableList<? extends Trait> getTraits(Direction direction) {
        return traits.getIfAbsentPut(direction, Lists.mutable.withInitialCapacity(TRAIT_CAPACITY)).asUnmodifiable();
    }

    @Override
    public @Unmodifiable MutableMap<Direction, MutableList<Trait>> allTraits() {
        return traits.asUnmodifiable();
    }

    @Override
    public @Nullable <T, C> T poxyCapability(BlockCapability<T, C> capability, @Nullable C context) {
        //TODO: capability caching
        if (context instanceof Direction) {
            return poxyCapability(traits.get(context), capability);
        } else {
            for (MutableList<? extends Trait> traits : traits) {
                T cap = poxyCapability(traits, capability);
                if (cap != null) return cap;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <T> @Nullable T poxyCapability(MutableList<? extends Trait> traits, BlockCapability<T, ?> capability) {
        for (Trait trait : traits) {
            if (trait instanceof PoxyTrait<?> poxyTrait && poxyTrait.getPoxyToken() == capability) {
                var cap = poxyTrait.poxyCapability();
                if (cap != null) return (T) cap;
            }
        }
        return null;
    }

    @ApiStatus.Internal
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
    public void addRejectSide(Direction direction) {
        rejectDirections.add(direction);
    }

    @Override
    public void removeRejectSide(Direction direction) {
        rejectDirections.remove(direction);
    }

    @Override
    public boolean isRejectedSide(Direction direction) {
        return rejectDirections.contains(direction);
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
        var connection = this.connections.remove(direction);
        if (connection == null) {
            throw new IllegalArgumentException("Trying to disconnect an unknown connection from: " + this + " at: " + direction);
        }
        var otherSide = connection.getOtherSide(this);
        otherSide.connections.remove(connection.getDirection(otherSide));
        connection.destroy();
    }

    @Override
    public void disconnect(NodeConnection connection) {
        Direction direction = connection.getDirection(this);
        disconnect(direction);
    }

    @Override
    public void disconnectAll() {
        for (Iterator<ConduitNodeConnection> iterator = connections.values().iterator(); iterator.hasNext(); ) {
            var connection = iterator.next();
            var otherSide = connection.getOtherSide(this);
            iterator.remove();
            otherSide.connections.remove(connection.getDirection(otherSide));
            connection.destroy();
        }
    }

    @Override
    public void visit(NetworkNodeVisitor visitor) {
        Deque<ConduitNetworkNode> nodes = new ArrayDeque<>();
        nodes.add(this);
        this.visited = new Object();
        while (!nodes.isEmpty()) {
            ConduitNetworkNode node = nodes.poll();
            visitor.visitNode(node);
            for (ConduitNodeConnection connection : node.connections) {
                ConduitNetworkNode otherSide = connection.getOtherSide(node);
                if (otherSide.visited == this.visited) {
                    continue;
                }
                otherSide.visited = this.visited;
                nodes.add(otherSide);
            }
        }
    }

    public void destroy() {
        this.initialized = false;
        // Disconnect this node from all its connections
        disconnectAll();

        // Check connectivity for each remaining node
        for (ConduitNetworkNode node : network.getNodes().rejectWith(Objects::equals, this)) {
            MutableSet<NetworkNode> component = Sets.mutable.empty();
            node.visit(component::add);
            // If the component size is less than the total nodes, it means the network is split
            if (component.size() < network.getNodes().size() - 1) {
                // Create a new network for the disconnected component
                ConduitNetwork newNetwork = ConduitNetwork.create();
                for (NetworkNode n : component) {
                    ((ConduitNetworkNode) n).setNetwork(newNetwork);
                }
            }
        }

        // Finally, remove this node from the original network
        network.removeNode(this);
        this.network = null;
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
        this.initialized = true;
        updateConnections();
        updateNode();
    }


    @Override
    public String toString() {
        return "ConduitNetworkNode{" +
                " pos=" + getPos() +
                '}';
    }
}
