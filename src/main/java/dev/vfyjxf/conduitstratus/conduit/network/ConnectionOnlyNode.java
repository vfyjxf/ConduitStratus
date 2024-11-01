//package dev.vfyjxf.conduitstratus.conduit.network;
//
//import dev.vfyjxf.conduitstratus.api.conduit.Conduit;
//import dev.vfyjxf.conduitstratus.api.conduit.network.Network;
//import dev.vfyjxf.conduitstratus.api.conduit.network.NodeConnection;
//import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
//import dev.vfyjxf.conduitstratus.api.conduit.trait.ConduitTrait;
//import dev.vfyjxf.conduitstratus.api.conduit.trait.ConduitTraitType;
//import dev.vfyjxf.conduitstratus.utils.Checks;
//import net.minecraft.core.BlockPos;
//import net.minecraft.core.Direction;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import org.eclipse.collections.api.RichIterable;
//import org.eclipse.collections.api.factory.Maps;
//import org.eclipse.collections.api.factory.Sets;
//import org.eclipse.collections.api.map.MutableMap;
//import org.eclipse.collections.api.set.MutableSet;
//import org.eclipse.collections.impl.map.mutable.MapAdapter;
//import org.jetbrains.annotations.Nullable;
//import org.jetbrains.annotations.Unmodifiable;
//
//import java.util.EnumMap;
//import java.util.Iterator;
//
////我写这个是干什么的？？？
//public class ConnectionOnlyNode implements NetworkNode, NetworkHolder {
//
//    @SuppressWarnings("rawtypes")
//    private static final MutableMap EMPTY_MAP = Maps.mutable.empty();
//
//    private Network network;
//    private final ServerLevel level;
//    private final Conduit conduit;
//    private final BlockEntity holder;
//    private final MutableMap<Direction, NodeConnection> connections = MapAdapter.adapt(new EnumMap<>(Direction.class));
//    private final MutableSet<Direction> rejectDirections = Sets.mutable.empty();
//
//    public ConnectionOnlyNode(Network network, Conduit conduit, BlockEntity holder) {
//        Checks.checkArgument(holder.getLevel() instanceof ServerLevel, "The given BlockEntity must be in a ServerLevel");
//        this.network = network;
//        this.conduit = conduit;
//        this.holder = holder;
//        this.level = (ServerLevel) holder.getLevel();
//    }
//
//    @Override
//    public Conduit getConduit() {
//        return conduit;
//    }
//
//    @Override
//    public BlockEntity getHolder() {
//        return holder;
//    }
//
//    @Override
//    public Network getNetwork() {
//        return network;
//    }
//
//    @Override
//    public void setNetwork(Network network) {
//        this.network = network;
//    }
//
//    @Override
//    public ServerLevel getLevel() {
//        return level;
//    }
//
//    @Override
//    public boolean hasTrait(ConduitTraitType type) {
//        return false;
//    }
//
//    @SuppressWarnings("unchecked")
//    @Override
//    public MutableMap<Direction, ConduitTrait> getTraits(ConduitTraitType type) {
//        return EMPTY_MAP.asUnmodifiable();
//    }
//
//    @SuppressWarnings("unchecked")
//    @Override
//    public MutableMap<ConduitTraitType, MutableMap<Direction, ? extends ConduitTrait>> allTraits() {
//        return EMPTY_MAP.asUnmodifiable();
//    }
//
//    @Override
//    public @Unmodifiable RichIterable<Direction> getDirections() {
//        return connections.keysView();
//    }
//
//    @Override
//    public MutableMap<Direction, NodeConnection> getConnectionsMap() {
//        return connections.asUnmodifiable();
//    }
//
//    @Override
//    public @Nullable NodeConnection getConnection(Direction direction) {
//        return connections.get(direction);
//    }
//
//    @Override
//    public @Unmodifiable RichIterable<NodeConnection> getConnections() {
//        return connections.valuesView();
//    }
//
//    @Nullable
//    @Override
//    public NetworkNode getNodeByDirection(Direction direction) {
//        NodeConnection connection = connections.get(direction);
//        if (connection == null) return null;
//
//        return connection.getOtherSide(this);
//    }
//
//    @Override
//    public void rejectDirection(Direction direction) {
//        this.rejectDirections.add(direction);
//    }
//
//    @Override
//    public boolean containsRejection(Direction direction) {
//        return this.rejectDirections.contains(direction);
//    }
//
//    @Override
//    public void removeRejection(Direction direction) {
//        this.rejectDirections.remove(direction);
//    }
//
//    @Override
//    public boolean connectable(Direction direction, NetworkNode node) {
//        BlockPos relative = getPos().relative(direction);
//
//        return !rejectDirections.contains(direction) &&
//                relative.equals(node.getPos()) &&
//                conduit.connectable(node.getConduit());
//    }
//
//    @Override
//    public boolean connected(Direction direction) {
//        return connections.containsKey(direction);
//    }
//
//    @Override
//    public void disconnect(Direction direction) {
//        NodeConnection remove = this.connections.remove(direction);
//        if (remove == null) {
//            throw new IllegalArgumentException("Trying to disconnect an unknown connection from: " + this + " at: " + direction);
//        }
//        remove.destroy();
//    }
//
//    @Override
//    public void disconnect(NodeConnection connection) {
//        Direction direction = connection.getDirection(this);
//        this.connections.remove(direction);
//        connection.destroy();
//    }
//
//    @Override
//    public void disconnectAll() {
//        for (Iterator<NodeConnection> iterator = connections.values().iterator(); iterator.hasNext(); ) {
//            NodeConnection connection = iterator.next();
//            iterator.remove();
//            connection.destroy();
//        }
//    }
//
//    @Override
//    public boolean canWorkWith(Direction direction) {
//        //TODO:
//        return false;
//    }
//
//    @Override
//    public void tick() {
//
//    }
//}
