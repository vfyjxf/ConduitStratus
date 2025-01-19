package dev.vfyjxf.conduitstratus.blockentity;

import dev.vfyjxf.conduitstratus.api.conduit.connection.ConduitNode;
import dev.vfyjxf.conduitstratus.api.conduit.connection.ConduitNodeId;
import dev.vfyjxf.conduitstratus.api.conduit.connection.ConnectionCalculation;
import dev.vfyjxf.conduitstratus.api.conduit.network.*;
import dev.vfyjxf.conduitstratus.init.values.ModValues;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.utility.Iterate;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

public abstract class NetworkBlockEntity extends BlockEntity implements ConduitNode {

    private static final Logger log = LoggerFactory.getLogger(NetworkBlockEntity.class);
    // TODO: clear this field
    protected final InitNetworkNode networkNode = NetworkBuilder.createInitNetworkNode(this);

    public @Nullable NetworkNode getNode() {
        return networkNode.getNode();
    }

    public final InitNetworkNode getNetworkNode() {
        return networkNode;
    }

    private BaseNetwork network;
    private boolean invalid;
    private boolean validated;
    private boolean destroyed;

    public NetworkBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }


    @Nullable
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


    public void markForUpdate() {
        this.requestModelDataUpdate();

        if (this.level != null && !this.isRemoved() && isLoaded()) {

            boolean alreadyUpdated = false;
            BlockState blockState = getBlockState();

            if (!alreadyUpdated) {
                this.level.sendBlockUpdated(this.worldPosition, blockState, blockState, Block.UPDATE_ALL);
            }
        }
    }

    public void markForSave() {
        if (this.level == null) {
            return;
        }

        if (this.level.isClientSide) {
            this.setChanged();
        } else {
            this.level.blockEntityChanged(this.worldPosition);
            this.setChanged();
        }
    }


    @Override
    public void setRemoved() {
        super.setRemoved();
        this.networkNode.destroy();

        destroyed = true;
        invalid = true;
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        this.networkNode.destroy();
    }

    public final boolean isLoaded() {
        return level != null && level.isLoaded(getBlockPos());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
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

    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

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

    }

    protected final MutableMap<Direction, ConduitNodeId> neighborNodes = Maps.mutable.empty();
    protected final MutableSet<ConduitNodeId> remoteNodes = Sets.mutable.empty();

    private ConduitNodeId nodeId;

    @Override
    public ConduitNodeId conduitId() {
        if (this.nodeId == null) {
            var level = Objects.requireNonNull(this.getLevel());
            this.nodeId = new ConduitNodeId(level.dimension(), this.getBlockPos());
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


    public boolean acceptsNeighbor(Direction direction) {
        return true;
    }

    @Override
    public boolean acceptsRemote(ConduitNodeId remote) {
        return false;
    }

    protected boolean collectRemoteNodes(MutableList<ConduitNodeId> remoteNodes) {
        return false;
    }


    @Override
    public void onDestroyed() {
        if (this.level.isClientSide) {
            return;
        }

        // only need to notify remote nodes since neighbor nodes will be updated by the block's updateShape
        for (var neighbor : this.remoteNodes) {
            ConduitNode neighborNode = this.findNodeAt(neighbor);
            if (neighborNode != null) {
                neighborNode.refreshRemote();
            }
        }

    }


    @Nullable
    private ConduitNode findNodeAt(BlockPos pos) {
        var level = Objects.requireNonNull(this.getLevel());
        return level.getCapability(ModValues.CONDUIT_NODE_CAP, pos);
    }

    @Nullable
    public ConduitNode findNodeAt(ConduitNodeId nodeId) {
        var level = Objects.requireNonNull(this.getLevel());
        if (!level.dimension().equals(nodeId.dimension())) {
            level = level.getServer().getLevel(nodeId.dimension());
        }

        if (level == null) {
            return null;
        }

        return level.getCapability(ModValues.CONDUIT_NODE_CAP, nodeId.pos());
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
    public boolean refreshRemote() {
        MutableList<ConduitNodeId> remoteNodes = Lists.mutable.empty();
        if (collectRemoteNodes(remoteNodes)) {
            boolean addded = false;
            MutableSet<ConduitNodeId> newNodes = Sets.mutable.empty();
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

            if (addded || this.remoteNodes.size() != newNodes.size()) {
                this.remoteNodes.clear();
                this.remoteNodes.addAll(newNodes);
                scheduleNetwork(1);
                return true;
            }
        }

        return false;
    }


    @Override
    public boolean refreshNeighbor() {
        MutableMap<Direction, ConduitNodeId> neighborNodes = Maps.mutable.withInitialCapacity(Direction.values().length);
        boolean added = false;
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = this.getBlockPos().relative(direction);
            if (!acceptsNeighbor(direction)) {
                continue;
            }

            ConduitNode neighborNode = this.findNodeAt(neighborPos);
            if (neighborNode == null) {
                continue;
            }
            ConduitNodeId neighborId = neighborNode.conduitId();
            boolean trueNeighbor = this.getLevel().dimension().equals(neighborId.dimension()) && neighborPos.equals(neighborId.pos());
            if (!trueNeighbor) {
                log.warn("Neighbor at {} is not next to {}", neighborPos, this.getBlockPos());
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
            return true;
        }

        return false;

    }

    private void refresh() {
        refreshNeighbor();
        refreshRemote();
        getLevel().neighborChanged(getBlockPos(), getBlockState().getBlock(), getBlockPos());
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
            getLevel().neighborChanged(getBlockPos(), getBlockState().getBlock(), getBlockPos());

            for (var neighbor : this.remoteNodes) {
                ConduitNode neighborNode = this.findNodeAt(neighbor);
                if (neighborNode != null) {
                    neighborNode.refreshRemote();
                }
            }

            this.markForSave();
            return false;
        }

        return true;
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

    public void setInvalid() {
        this.invalid = true;
    }
}
