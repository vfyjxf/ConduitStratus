package dev.vfyjxf.conduitstratus.blockentity;

import dev.vfyjxf.conduitstratus.api.conduit.connection.ConduitNode;
import dev.vfyjxf.conduitstratus.api.conduit.connection.ConduitNodeId;
import dev.vfyjxf.conduitstratus.api.conduit.network.InitNetworkNode;
import dev.vfyjxf.conduitstratus.api.conduit.network.Network;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkBuilder;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import dev.vfyjxf.conduitstratus.conduit.network.NetworkHolder;
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
    protected final InitNetworkNode networkNode = NetworkBuilder.createInitNetworkNode(this);

    public NetworkBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public final InitNetworkNode getNetworkNode() {
        return networkNode;
    }

    public @Nullable Network getNetwork() {
        return networkNode.getNetwork();
    }


    @Override
    public void setNetwork(Network network) {
        if (!this.networkNode.available()) {
            throw new IllegalStateException("Network is not initialized yet or it was destroyed");
        }
        if (!(this.getNode() instanceof NetworkHolder networkHolder)) {
            throw new IllegalStateException("Not a network holder");
        }

        networkHolder.setNetwork(network);

    }

    public @Nullable NetworkNode getNode() {
        return networkNode.getNode();
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
    public boolean refreshRemote() {
        this.remoteNodes.clear();
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
                return true;
            }
        }

        return false;
    }


    @Override
    public boolean refreshNeighbor() {
        this.neighborNodes.clear();
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
            return true;
        }

        return false;

    }


    @Override
    public boolean valid() {
        return false;
    }

    @Override
    public boolean initializing() {
        return false;
    }

    @Override
    public void setValid(boolean valid) {

    }

    @Override
    public void refresh() {

    }
}
