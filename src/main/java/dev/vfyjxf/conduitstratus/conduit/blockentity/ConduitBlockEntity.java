package dev.vfyjxf.conduitstratus.conduit.blockentity;

import dev.vfyjxf.conduitstratus.api.conduit.Conduit;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkBuilder;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import dev.vfyjxf.conduitstratus.api.conduit.trait.TraitType;
import dev.vfyjxf.conduitstratus.blockentity.NetworkBlockEntity;
import dev.vfyjxf.conduitstratus.client.models.ModelProperties;
import dev.vfyjxf.conduitstratus.conduit.ConduitConnections;
import dev.vfyjxf.conduitstratus.conduit.block.ConduitShapes;
import dev.vfyjxf.conduitstratus.init.values.ModValues;
import dev.vfyjxf.conduitstratus.utils.EnumConstant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class ConduitBlockEntity extends NetworkBlockEntity {

    private Conduit conduit;
    private final EnumSet<Direction> rejectDirections = EnumSet.noneOf(Direction.class);
    private final ConduitConnections connections = new ConduitConnections();

    public ConduitBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModValues.conduitBlockEntity.get(), pos, blockState);
    }

    public static <T, C> ICapabilityProvider<? extends ConduitBlockEntity, C, T> getCapabilityProvider(BlockCapability<T, C> capability) {
        return ((be, context) -> {
            var node = be.networkNode.getNode();
            if (node != null) {
                return node.poxyCapability(capability, context);
            }
            return null;
        });

    }

    public Conduit getConduit() {
        return conduit;
    }

    public void setConduit(Conduit conduit) {
        this.conduit = conduit;
    }

    public void addRejectDirection(Direction direction) {
        rejectDirections.add(direction);
    }

    public void removeRejectDirection(Direction direction) {
        rejectDirections.remove(direction);
    }

    public boolean isDirectionRejected(Direction direction) {
        return rejectDirections.contains(direction);
    }

    public VoxelShape getShape() {
        return ConduitShapes.getShape(connections);
    }

    @Override
    public ModelData getModelData() {
        return ModelData.builder()
                .with(ModelProperties.CONDUIT_CONNECTION, connections)
                .build();
    }


    @ApiStatus.Internal
    public void updateConnections() {
        resetConnection();
        setChanged();
    }

    public ConduitConnections getConnections() {
        return connections;
    }

    public void addTrait(TraitType type, Direction side) {
        var node = getNode();
        if (node != null) {
            node.addTrait(side, type.getFactory().create(type, node, side));
        }
        connections.addTrait(side);
        markForUpdate();
        markForSave();
    }

    private void resetConnection() {
        EnumSet<Direction> updated = EnumSet.noneOf(Direction.class);
        for (Direction direction : EnumConstant.directions) {
            if (rejectDirections.contains(direction) || connections.hasTrait(direction)) continue;
            BlockPos pos = this.getBlockPos().relative(direction);
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ConduitBlockEntity conduitBlockEntity) {
                Direction opposite = direction.getOpposite();
                if (conduitBlockEntity.isDirectionRejected(opposite) || conduitBlockEntity.connections.hasTrait(opposite)) {
                    continue;
                }
                updated.add(direction);
            }
        }
        connections.setConnections(updated);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(final HolderLookup.Provider provider) {
        return connections.writeToTag(new CompoundTag());
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        networkNode.loadData(tag);
        connections.fromTag(tag);
        if (level != null) {
            requestModelDataUpdate();
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        networkNode.saveData(tag);
        connections.writeToTag(tag);
    }

    private void onInitTick() {
        assert level != null;
        this.networkNode.build(level, this);
        //connect to other conduit
        for (Direction direction : EnumConstant.directions) {
            if (rejectDirections.contains(direction)) continue;
            BlockPos pos = this.getBlockPos().relative(direction);
            NetworkNode self = this.networkNode.getNode();
            if (self == null || self.connected(direction) || !self.getTraits(direction).isEmpty()) continue;
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ConduitBlockEntity conduitBlockEntity) {
                NetworkNode node = conduitBlockEntity.getNetworkNode().getNode();
                //todo:check conduit connectable
                Direction opposite = direction.getOpposite();
                if (node != null && !node.connected(opposite) && node.getTraits(opposite).isEmpty()) {
                    NetworkBuilder.createConnection(this.networkNode.getNode(), node, direction);
                }
            }
        }
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
