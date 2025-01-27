package dev.vfyjxf.conduitstratus.conduit.blockentity;

import dev.vfyjxf.conduitstratus.api.conduit.Conduit;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkBuilder;
import dev.vfyjxf.conduitstratus.api.conduit.trait.TraitType;
import dev.vfyjxf.conduitstratus.blockentity.NetworkBlockEntity;
import dev.vfyjxf.conduitstratus.client.models.ModelProperties;
import dev.vfyjxf.conduitstratus.conduit.ConnectionState;
import dev.vfyjxf.conduitstratus.conduit.block.ConduitShapes;
import dev.vfyjxf.conduitstratus.init.values.ModValues;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;


public class ConduitBlockEntity extends NetworkBlockEntity {

    private static final Logger logger = LoggerFactory.getLogger("ConduitStratus-ConduitBlockEntity");
    private Conduit conduit;
    private final EnumSet<Direction> rejectDirections = EnumSet.noneOf(Direction.class);
    private final ConnectionState connectionState = new ConnectionState();


    public ConduitBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModValues.conduitBlockEntity.get(), pos, blockState);
    }

    @SuppressWarnings("unchecked")
    public static <T, C> ICapabilityProvider<? extends ConduitBlockEntity, C, T> getCapabilityProvider(BlockCapability<T, C> capability) {
        return ((be, context) -> {
            if (capability == ModValues.CONDUIT_NODE_CAP) {
                return (T) be.conduitNode();
            }
            var node = be.networkNode();
            if (node != null) {
                return node.poxyCapability(capability, context);
            }
            return null;
        });

    }

    @Override
    public BlockEntity getBlockEntity() {
        return this;
    }

    @Override
    public Conduit getConduit() {
        return conduit;
    }

    @Override
    public void setConduit(Conduit conduit) {
        this.conduit = conduit;
    }

    @Override
    public boolean acceptsNeighbor(Direction direction) {
        return !rejectDirections.contains(direction) && !connectionState.hasTrait(direction);
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
        return ConduitShapes.getShape(connectionState);
    }

    @Override
    public ModelData getModelData() {
        return ModelData.builder()
                .with(ModelProperties.CONDUIT_CONNECTION, connectionState)
                .build();
    }

    public void updateConnectionState() {
        if (conduitNode() != null) {
            connectionState.setConnections(conduitNode().connectedDirections());
        }
    }

    @Override
    public void connectionChange() {
        updateConnectionState();
        markForUpdate();
        markForSave();
    }

    public ConnectionState getConnectionState() {
        return connectionState;
    }

    public void addTrait(TraitType type, Direction side) {
//        networkNode.addTrait(side, );
        connectionState.addTrait(side);
        markForUpdate();
        markForSave();
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(final HolderLookup.Provider provider) {
        return connectionState.writeToTag(new CompoundTag(), true);
    }


    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        connectionState.writeToTag(tag, false);
        tag.putIntArray("rejectDirections", rejectDirections.stream().mapToInt(Direction::get3DDataValue).toArray());
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        connectionState.fromTag(tag);
        if (!tag.contains("conduitConnections")) {
            updateConnectionState();
        }
        int[] rejectDirections = tag.getIntArray("rejectDirections");
        for (int i : rejectDirections) {
            this.rejectDirections.add(Direction.from3DDataValue(i));
        }

        markForUpdate();
    }


    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        this.connectionState.clear();
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        this.connectionState.clear();
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();
        NetworkBuilder.onInitTick(this, () -> {
            conduitNode().scheduleNetwork(0);
        });
    }
}
