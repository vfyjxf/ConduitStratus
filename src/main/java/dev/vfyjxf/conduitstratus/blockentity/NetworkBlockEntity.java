package dev.vfyjxf.conduitstratus.blockentity;

import dev.vfyjxf.conduitstratus.api.conduit.ConduitEntity;
import dev.vfyjxf.conduitstratus.api.conduit.connection.ConduitNode;
import dev.vfyjxf.conduitstratus.api.conduit.connection.ConduitNodeId;
import dev.vfyjxf.conduitstratus.api.conduit.connection.RemoteNodeProvider;
import dev.vfyjxf.conduitstratus.api.conduit.device.AttachableDevice;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import dev.vfyjxf.conduitstratus.conduit.network.ConduitNetworkNode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.eclipse.collections.api.list.MutableList;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class NetworkBlockEntity extends BlockEntity implements ConduitEntity {

    private static final Logger log = LoggerFactory.getLogger(NetworkBlockEntity.class);

    private ConduitNetworkNode networkNode = new ConduitNetworkNode(this);

    public NetworkBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Nullable
    @Contract(pure = true)
    public ConduitNode conduitNode() {
        return networkNode;
    }

    @Nullable
    @Contract(pure = true)
    public NetworkNode networkNode() {
        return networkNode;
    }

    @Override
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

    @Override
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
    public boolean acceptsRemote(ConduitNodeId remote) {
        if (networkNode == null) return false;
        boolean accept = false;
        for (var iterator = networkNode.allDevices().iterator(); iterator.hasNext() && !accept; ) {
            AttachableDevice device = iterator.next();
            if (device instanceof RemoteNodeProvider provider) {
                accept = provider.acceptsRemote(remote);
            }
        }
        return accept;
    }

    @Override
    public boolean collectRemoteNodes(MutableList<ConduitNodeId> remoteNodes) {
        if (networkNode == null) return false;
        boolean collected = false;
        for (AttachableDevice device : networkNode.allDevices()) {
            if (device instanceof RemoteNodeProvider provider) {
                collected = provider.collectRemoteNodes(remoteNodes);
            }
        }
        return collected;
    }


    @Override
    public void setRemoved() {
        super.setRemoved();
        if (networkNode != null) {
            this.networkNode.destroy(true);
        }
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        if (networkNode != null) {
            this.networkNode.destroy(false);
        }
    }

    public final boolean isLoaded() {
        return level != null && level.isLoaded(getBlockPos());
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (networkNode != null) {
            networkNode.saveData(tag, registries);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (networkNode != null) {
            networkNode.loadData(tag, registries);
        }
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();
        if (level.isClientSide) {
            this.networkNode = null;
        }
    }

}
