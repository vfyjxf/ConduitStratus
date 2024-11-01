package dev.vfyjxf.conduitstratus.conduit.blockentity;

import dev.vfyjxf.conduitstratus.api.conduit.network.InitNetworkNode;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkBuilder;
import dev.vfyjxf.conduitstratus.conduit.values.ModValues;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.NotNull;

public class ConduitBlockEntity extends BlockEntity {

    private final InitNetworkNode networkNode;

    public ConduitBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModValues.conduitBlockEntity.get(), pos, blockState);
        this.networkNode = NetworkBuilder.createInitNetworkNode(this);
    }

    public static <T, C> ICapabilityProvider<ConduitBlockEntity, C, T> getCapabilityProvider(BlockCapability<T, C> capability) {
        return ((be, context) -> {
            var node = be.networkNode.getNode();
            if (node != null) {
                return node.poxyCapability(capability, context);
            }
            return null;
        });
    }

    public InitNetworkNode getNetworkNode() {
        return networkNode;
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        networkNode.loadData(tag);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        networkNode.saveData(tag);
    }

    private void onInitTick() {
        assert level != null;
        this.networkNode.build(level, this);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        this.networkNode.destroy();
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();
        NetworkBuilder.onInitTick(this, this::onInitTick);
    }


}
