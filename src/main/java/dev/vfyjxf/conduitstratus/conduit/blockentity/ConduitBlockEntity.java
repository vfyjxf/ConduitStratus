package dev.vfyjxf.conduitstratus.conduit.blockentity;

import dev.vfyjxf.conduitstratus.api.conduit.Conduit;
import dev.vfyjxf.conduitstratus.api.conduit.connection.ConduitNode;
import dev.vfyjxf.conduitstratus.api.conduit.connection.ConduitNodeId;
import dev.vfyjxf.conduitstratus.api.conduit.connection.ConnectionCalculation;
import dev.vfyjxf.conduitstratus.api.conduit.network.Network;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkBuilder;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import dev.vfyjxf.conduitstratus.api.conduit.trait.TraitType;
import dev.vfyjxf.conduitstratus.blockentity.NetworkBlockEntity;
import dev.vfyjxf.conduitstratus.client.models.ModelProperties;
import dev.vfyjxf.conduitstratus.conduit.ConnectionState;
import dev.vfyjxf.conduitstratus.conduit.block.ConduitShapes;
import dev.vfyjxf.conduitstratus.conduit.network.NetworkHolder;
import dev.vfyjxf.conduitstratus.init.values.ModValues;
import dev.vfyjxf.conduitstratus.utils.EnumConstant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.utility.Iterate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;


public class ConduitBlockEntity extends NetworkBlockEntity implements ConduitNode {

    private static final Logger logger = LoggerFactory.getLogger("ConduitStratus-ConduitBlockEntity");
    private Conduit conduit;
    private final EnumSet<Direction> rejectDirections = EnumSet.noneOf(Direction.class);
    private final ConnectionState connectionState = new ConnectionState();

    private ConduitNodeId nodeId;
    private boolean invalid = false;
    private final MutableMap<ConduitNodeId, ConduitNode> conduitConnections = Maps.mutable.empty();
    private final MutableMap<ConduitNodeId, ConduitNode> incomingRemoteConnections = Maps.mutable.empty();


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
    public ModelData getModelData() {
        return ModelData.builder()
                .with(ModelProperties.CONDUIT_CONNECTION, connectionState)
                .build();
    }

    @ApiStatus.Internal
    public void updateConnections() {
        resetConnection();
        setChanged();
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

    //todo:impl reject direction feature
    private void resetConnection() {
        EnumSet<Direction> updated = EnumSet.noneOf(Direction.class);
        EnumSet<Direction> old = connectionState.connectionSides();
        for (Direction direction : EnumConstant.directions) {
            if (rejectDirections.contains(direction) || connectionState.hasTrait(direction)) continue;
            BlockPos pos = this.getBlockPos().relative(direction);
            BlockEntity blockEntity = level.getBlockEntity(pos);
            //todo:使用cap而不是强制要求为ConduitBlockEntity
            if (blockEntity instanceof ConduitBlockEntity conduitBlockEntity) {
                Direction opposite = direction.getOpposite();
                if (conduitBlockEntity.isDirectionRejected(opposite) || conduitBlockEntity.connectionState.hasTrait(opposite)) {
                    continue;
                }
                updated.add(direction);
            }

        }
        connectionState.setConnections(updated);
        if (!updated.equals(old)) {
            //fixme:暂停下一刻的路径精算
        }

        refreshConnection();
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
        return connectionState.writeToTag(new CompoundTag());
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        networkNode.saveData(tag);
        connectionState.writeToTag(tag);
        tag.putIntArray("rejectDirections", rejectDirections.stream().mapToInt(Direction::get3DDataValue).toArray());
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        networkNode.loadData(tag);
        connectionState.fromTag(tag);
        int[] rejectDirections = tag.getIntArray("rejectDirections");
        for (int i : rejectDirections) {
            this.rejectDirections.add(Direction.from3DDataValue(i));
        }
        if (level != null) {
            requestModelDataUpdate();
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Nullable
    private ConduitNode loadRemoteNode(ConduitNodeId id) {
        throw new UnsupportedOperationException("Not implemented yet");
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
        if (this.networkNode.available()) {
            this.networkNode.getNetwork().destroy();
        }
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();
        NetworkBuilder.onInitTick(this, this::onInitTick);
    }

    private void onInitTick() {
        assert level != null;
        updateConnections();
        this.networkNode.build(level, this);
        ConnectionCalculation.getInstance().addIdleNode(this);
    }

    // conduit nodes

    @Override
    public boolean valid() {
        return !this.invalid;
    }

    @Override
    public boolean initializing() {
        return !this.networkNode.available();
    }

    @Override
    public void setValid(boolean valid) {
        for (ConduitNode connection : this.conduitConnections) {
            connection.refresh();
        }
        for (ConduitNode connection : this.incomingRemoteConnections) {
            connection.refresh();
        }
        this.invalid = !valid;
    }

    @Override
    public void refresh() {
        this.refreshConnection();
    }

    @Override
    public List<ConduitNodeId> neighbors() {
        List<ConduitNodeId> neighbors = Lists.mutable.withInitialCapacity(conduitConnections.size() + incomingRemoteConnections.size());
        Iterate.addAllTo(conduitConnections.keysView(), neighbors);
        Iterate.addAllTo(incomingRemoteConnections.keysView(), neighbors);
        return neighbors;
    }

    @Override
    public ConduitNodeId conduitId() {
        if (this.nodeId == null) {
            this.nodeId = new ConduitNodeId(this.getLevel().dimension(), this.getBlockPos());
        }
        return this.nodeId;
    }

    private void refreshConnection() {
        HashSet<ConduitNodeId> prevConnections = new HashSet<>();
        Iterate.addAllTo(this.conduitConnections.keysView(), prevConnections);
        this.conduitConnections.clear();
        for (Direction direction : this.connectionState.connectionSides()) {
            Level level = getLevel();
            BlockPos pos = this.getBlockPos().relative(direction);
            ConduitNode node = level.getCapability(ModValues.CONDUIT_NODE_CAP, pos);
            if (node == null) {
                logger.error("Could not find conduit node at {}", pos);
                continue;
            }

            if (!node.valid()) {
                logger.error("Could not connect to invalid conduit node at {}", pos);
                continue;
            }

            this.conduitConnections.put(node.conduitId(), node);
        }

        HashSet<ConduitNodeId> postConnections = new HashSet<>();
        Iterate.addAllTo(this.conduitConnections.keysView(), postConnections);

        if (prevConnections.equals(postConnections)) {
            return;
        }

        if (this.networkNode.available()) {
            this.networkNode.getNode().resetNetwork();
            ConnectionCalculation.getInstance().addIdleNode(this);
        }
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
}
