package dev.vfyjxf.conduitstratus.blockentity;

import dev.vfyjxf.conduitstratus.api.conduit.network.InitNetworkNode;
import dev.vfyjxf.conduitstratus.api.conduit.network.Network;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkBuilder;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class NetworkBlockEntity extends BlockEntity {

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

    public @Nullable NetworkNode getNode() {
        return networkNode.getNode();
    }

    public void markForUpdate() {
        this.requestModelDataUpdate();

        if (this.level != null && !this.isRemoved() && isLoaded()) {

            boolean alreadyUpdated = false;
            BlockState blockState = getBlockState();

            if (!alreadyUpdated) {
                this.level.sendBlockUpdated(this.worldPosition, blockState, blockState, Block.UPDATE_NEIGHBORS);
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


    public final boolean isLoaded() {
        return level != null && level.isLoaded(getBlockPos());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
    }
}
