package dev.vfyjxf.conduitstratus.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.ConduitEntity;
import dev.vfyjxf.conduitstratus.api.conduit.connection.ConduitNode;
import dev.vfyjxf.conduitstratus.api.conduit.connection.ConduitNodeId;
import dev.vfyjxf.conduitstratus.api.conduit.connection.ConnectionCalculation;
import dev.vfyjxf.conduitstratus.api.conduit.network.BaseNetwork;
import dev.vfyjxf.conduitstratus.api.conduit.network.Network;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkChannels;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkStatus;
import dev.vfyjxf.conduitstratus.api.conduit.trait.PoxyTrait;
import dev.vfyjxf.conduitstratus.api.conduit.trait.Trait;
import dev.vfyjxf.conduitstratus.api.conduit.trait.TraitType;
import dev.vfyjxf.conduitstratus.init.values.ModValues;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.utility.Iterate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

@ApiStatus.Internal
public class ConduitNetworkNode implements ConduitNode, NetworkNode {

    private static final Logger log = LoggerFactory.getLogger(ConduitNetworkNode.class);

    /////////////////////////////////////////////////////
    //********            NetworkNode         *********//
    ////////////////////////////////////////////////////

    public static final int TRAIT_CAPACITY = 3;

    private BaseNetwork network;
    private final ConduitEntity holder;
    private final MutableMap<Direction, MutableList<Trait>> traits = Maps.mutable.empty();

    public ConduitNetworkNode(ConduitEntity holder) {
        this.holder = holder;
    }

    @Override
    public Network getEffectiveNetwork() {
        if (!(network instanceof ConduitNetwork conduitNetwork) || network.status() == NetworkStatus.Destroyed) {
            if (network == null) {
                throw new IllegalStateException("Network is not available yet");
            }
            throw new IllegalStateException("Network was destroyed");
        }
        return conduitNetwork;
    }

    @Override
    public ConduitEntity getHolder() {
        return holder;
    }

    @Override
    public BaseNetwork getNetwork() {
        if (network != null && network.status() == NetworkStatus.Destroyed) {
            network = null;
        }
        return network;
    }

    @Override
    public void setNetwork(@Nullable BaseNetwork network) {
        this.network = network;
    }

    @Override
    public ServerLevel getLevel() {
        return holder.serverLevel();
    }

    @Override
    public void addTrait(Direction direction, Trait trait) {
        traits.getIfAbsentPut(direction, Lists.mutable.withInitialCapacity(TRAIT_CAPACITY)).add(trait);
        if (online()) {
            NetworkChannels<Trait> channel = getEffectiveNetwork().getChannel(trait.getHandleType());
            channel.addTrait(trait);
        }
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

    @Override
    public void destroy(boolean remove) {
        if (remove) {
            this.destroyed = true;
            this.invalid = true;
        }
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

    @Override
    public void saveData(CompoundTag tag, HolderLookup.Provider registries) {
        CompoundTag neighborNodes = new CompoundTag();
        for (var entry : this.neighborNodes.entrySet()) {
            neighborNodes.put(entry.getKey().name(), entry.getValue().toTag());
        }

        ListTag remoteNodes = new ListTag();
        for (ConduitNodeId remote : this.remoteNodes) {
            remoteNodes.add(remote.toTag());
        }

        tag.put("NeighborNodes", neighborNodes);
        tag.put("RemoteNodes", remoteNodes);

        int[] connectableDirections = tag.getIntArray("connectableDirections");
        for (int i : connectableDirections) {
            this.connectedDirections.add(Direction.from3DDataValue(i));
        }
    }

    @Override
    public void loadData(CompoundTag tag, HolderLookup.Provider registries) {
        this.neighborNodes.clear();
        this.remoteNodes.clear();
        if (tag.contains("NeighborNodes")) {
            CompoundTag neighborNodes = tag.getCompound("NeighborNodes");
            for (String key : neighborNodes.getAllKeys()) {
                Direction direction = Direction.valueOf(key);
                ConduitNodeId neighborId = ConduitNodeId.fromTag(neighborNodes.getCompound(key));
                this.neighborNodes.put(direction, neighborId);
            }
        }

        if (tag.contains("RemoteNodes")) {
            ListTag remoteNodes = tag.getList("RemoteNodes", 10);
            for (int i = 0; i < remoteNodes.size(); i++) {
                this.remoteNodes.add(ConduitNodeId.fromTag(remoteNodes.getCompound(i)));
            }
        }

        tag.putIntArray("connectableDirections", connectedDirections.stream().mapToInt(Direction::get3DDataValue).toArray());
    }

    @Override
    public void setConnectedDirections(Collection<Direction> directions) {
        this.connectedDirections.clear();
        this.connectedDirections.addAll(directions);
        this.holder.connectionChange();
    }

    /////////////////////////////////////////////////////
    //********            ConduitNode         *********//
    ////////////////////////////////////////////////////


    private boolean invalid;
    private boolean validated;
    private boolean destroyed;

    private final MutableMap<Direction, ConduitNodeId> neighborNodes = Maps.mutable.empty();
    private final MutableSet<ConduitNodeId> remoteNodes = Sets.mutable.withInitialCapacity(1);
    private final EnumSet<Direction> connectedDirections = EnumSet.noneOf(Direction.class);

    private ConduitNodeId nodeId;

    @Override
    public ConduitNodeId conduitId() {
        if (this.nodeId == null) {
            var level = Objects.requireNonNull(this.getLevel());
            this.nodeId = new ConduitNodeId(level.dimension(), this.getPos());
        }
        return this.nodeId;
    }

    @Override
    public List<ConduitNodeId> adjacentNodes() {
        List<ConduitNodeId> neighbors = Lists.mutable.withInitialCapacity(neighborNodes.size() + remoteNodes.size());
        Iterate.addAllTo(neighborNodes.valuesView(), neighbors);
        Iterate.addAllTo(remoteNodes, neighbors);
        return neighbors;
    }

    @Override
    public Collection<Direction> connectedDirections() {
        return neighborNodes.keySet();
    }

    @Override
    public boolean acceptsNeighbor(Direction direction) {
        return holder.acceptsNeighbor(direction);
    }

    @Override
    public void removeFromLevel() {
        // only need to notify remote nodes since neighbor nodes will be updated by the block's updateShape
        for (var neighbor : this.remoteNodes) {
            ConduitNode neighborNode = this.findNodeAt(neighbor);
            if (neighborNode != null) {
                neighborNode.onRemoteChanged();
            }
        }
    }

    @Nullable
    private ConduitNode findNodeAt(BlockPos pos) {
        return getLevel().getCapability(ModValues.CONDUIT_NODE_CAP, pos);
    }

    @Nullable
    public ConduitNode findNodeAt(ConduitNodeId nodeId) {
        var level = this.getLevel();
        if (!level.dimension().equals(nodeId.dimension())) {
            level = level.getServer().getLevel(nodeId.dimension());
        }

        if (level == null) {
            return null;
        }

        return level.getCapability(ModValues.CONDUIT_NODE_CAP, nodeId.pos());
    }

    @Override
    public boolean acceptsRemote(ConduitNodeId remote) {
        return holder.acceptsRemote(remote);
    }

    @Override
    public void onRemoteChanged() {
        refreshRemote();
    }

    @Override
    public boolean refreshRemote() {
        MutableList<ConduitNodeId> remoteNodes = Lists.mutable.empty();
        boolean addded = false;
        MutableSet<ConduitNodeId> newNodes = Sets.mutable.empty();
        if (holder.collectRemoteNodes(remoteNodes)) {
            for (ConduitNodeId remote : remoteNodes) {
                ConduitNode neighborNode = this.findNodeAt(remote);
                if (neighborNode == null || !neighborNode.acceptsRemote(this.conduitId())) {
                    continue;
                }
                if (this.remoteNodes.contains(remote)) {
                    addded = true;
                }
                newNodes.add(remote);
            }
        }

        if (addded || this.remoteNodes.size() != newNodes.size()) {
            this.remoteNodes.clear();
            this.remoteNodes.addAll(newNodes);
            scheduleNetwork(1);
            return true;
        }

        return false;
    }

    @Override
    public boolean refreshNeighbor() {
        MutableMap<Direction, ConduitNodeId> neighborNodes = Maps.mutable.withInitialCapacity(Direction.values().length);
        boolean added = false;
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = this.getPos().relative(direction);
            if (!acceptsNeighbor(direction)) {
                continue;
            }

            ConduitNode neighborNode = this.findNodeAt(neighborPos);
            if (neighborNode == null) {
                continue;
            }
            ConduitNodeId neighborId = neighborNode.conduitId();
            boolean trueNeighbor = getLevel().dimension().equals(neighborId.dimension()) && neighborPos.equals(neighborId.pos());
            if (!trueNeighbor) {
                log.warn("Neighbor at {} is not next to {}", neighborPos, this.getPos());
                continue;
            }

            if (neighborNode.acceptsNeighbor(direction.getOpposite())) {
                neighborNodes.put(direction, neighborId);
                added = true;
            }

        }

        if (added || this.neighborNodes.size() != neighborNodes.size()) {
            this.neighborNodes.clear();
            this.neighborNodes.putAll(neighborNodes);
            scheduleNetwork(1);
            setConnectedDirections(neighborNodes.keySet());
            return true;
        }

        return false;

    }

    @Override
    public boolean online() {
        return network instanceof ConduitNetwork && network.status() == NetworkStatus.Ready;
    }

    @Override
    public boolean isInvalid() {
        if (destroyed) {
            return true;
        }
        if (invalid) {
            return true;
        }

        return !validate();
    }

    @Override
    public boolean validate() {
        if (destroyed) {
            return false;
        }
        if (validated) {
            return true;
        }

        boolean valid = true;

        for (var entry : this.neighborNodes.entrySet()) {
            ConduitNodeId neighborId = entry.getValue();
            Direction direction = entry.getKey();
            ConduitNode neighbor = this.findNodeAt(neighborId);
            if (neighbor == null) {
                log.warn("Neighbor at {} is not found", neighborId);
                valid = false;
                break;
            }
            if (!neighbor.acceptsNeighbor(direction.getOpposite())) {
                log.warn("Neighbor at {} does not accept connection from {}", neighborId, direction);
                valid = false;
                break;
            }
        }

        if (!valid) {
            validated = false;
            refresh();
            return false;
        }

        for (ConduitNodeId remote : this.remoteNodes) {
            ConduitNode remoteNode = this.findNodeAt(remote);
            if (remoteNode == null) {
                log.warn("Remote at {} is not found", remote);
                valid = false;
                break;
            }
            if (!remoteNode.acceptsRemote(this.conduitId())) {
                log.warn("Remote at {} does not accept connection from {}", remote, this.conduitId());
                valid = false;
                break;
            }
        }

        if (!valid) {
            validated = false;
            refresh();
            return false;
        }

        validated = true;
        return validateBiDirectional();
    }

    private void refresh() {
        refreshNeighbor();
        refreshRemote();
        getLevel().neighborChanged(getPos(), holder.getBlockEntity().getBlockState().getBlock(), getPos());
        validateBiDirectional();
    }

    private boolean validateBiDirectional() {
        boolean valid = true;
        outer:
        for (var neighbor : this.adjacentNodes()) {
            ConduitNode neighborNode = this.findNodeAt(neighbor);
            if (neighborNode == null) {
                continue;
            }

            for (var neighborNeighbor : neighborNode.adjacentNodes()) {
                if (neighborNeighbor.equals(this.conduitId())) {
                    continue outer;
                }
            }

            valid = false;
            break;
        }

        if (!valid) {
            this.invalid = true;
            getLevel().neighborChanged(getPos(), holder.getBlockEntity().getBlockState().getBlock(), getPos());

            for (var neighbor : this.remoteNodes) {
                ConduitNode neighborNode = this.findNodeAt(neighbor);
                if (neighborNode != null) {
                    neighborNode.onRemoteChanged();
                }
            }

            holder.markForSave();
            return false;
        }

        return true;
    }

    @Override
    public void scheduleNetwork(int delay) {
        BaseNetwork network = this.getNetwork();
        if (network != null) {
            network.destroy();
        }
        ConnectionCalculation.getInstance().addIdleNode(this, delay);
    }

    @Override
    public void setInvalid() {
        this.invalid = true;
    }

}
