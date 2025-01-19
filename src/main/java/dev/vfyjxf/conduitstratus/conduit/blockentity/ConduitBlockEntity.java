package dev.vfyjxf.conduitstratus.conduit.blockentity;

import dev.vfyjxf.conduitstratus.api.conduit.Conduit;
import dev.vfyjxf.conduitstratus.api.conduit.connection.ConduitNode;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkBuilder;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;


public class ConduitBlockEntity extends NetworkBlockEntity implements ConduitNode {

    private static final Logger logger = LoggerFactory.getLogger("ConduitStratus-ConduitBlockEntity");
    private Conduit conduit;
    private final EnumSet<Direction> rejectDirections = EnumSet.noneOf(Direction.class);
    private final ConnectionState connectionState = new ConnectionState();

    private boolean invalid = false;


    public ConduitBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModValues.conduitBlockEntity.get(), pos, blockState);
    }

    public static <T, C> ICapabilityProvider<? extends ConduitBlockEntity, C, T> getCapabilityProvider(BlockCapability<T, C> capability) {
        return ((be, context) -> {
            if (capability == ModValues.CONDUIT_NODE_CAP) {
                return (T) be;
            }
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
        return ConduitShapes.getShape(connectionState);
    }


    @Override
    public boolean acceptsNeighbor(Direction direction) {
        return !rejectDirections.contains(direction) && !connectionState.hasTrait(direction);
    }

    @Override
    public ModelData getModelData() {
        return ModelData.builder()
                .with(ModelProperties.CONDUIT_CONNECTION, connectionState)
                .build();
    }

    private void updateConnectionState() {
        if (this.neighborNodes.isEmpty()) {
            connectionState.clear();
            return;
        }
        connectionState.setConnections(EnumSet.copyOf(this.neighborNodes.keySet()));
    }

    @Override
    public boolean refreshNeighbor() {
        if (super.refreshNeighbor()) {
            updateConnectionState();
            markForUpdate();
            markForSave();
            return true;
        }
        return false;
    }

    public ConnectionState getConnectionState() {
        return connectionState;
    }

    public void addTrait(TraitType type, Direction side) {
        var node = getNode();
        if (node != null) {
            node.addTrait(side, type.getFactory().create(type, node, side));
        }
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
        NetworkNode node = getNode();
        if (node != null) {

        }
        return connectionState.writeToTag(new CompoundTag(), true);
    }


    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        networkNode.saveData(tag);
        connectionState.writeToTag(tag, false);
        tag.putIntArray("rejectDirections", rejectDirections.stream().mapToInt(Direction::get3DDataValue).toArray());
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        networkNode.loadData(tag);
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
        NetworkBuilder.onInitTick(this, this::onInitTick);
    }

    private void onInitTick() {
        assert level != null;
        this.networkNode.build(level, this);
        this.scheduleNetwork(0);
    }


}
