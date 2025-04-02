package dev.vfyjxf.conduitstratus.conduit.device.remote;

import dev.vfyjxf.conduitstratus.api.conduit.DeviceType;
import dev.vfyjxf.conduitstratus.api.conduit.TickStatus;
import dev.vfyjxf.conduitstratus.api.conduit.connection.ConduitNodeId;
import dev.vfyjxf.conduitstratus.api.conduit.device.AttachableDevice;
import dev.vfyjxf.conduitstratus.api.conduit.device.BasicAttachableDevice;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import dev.vfyjxf.conduitstratus.blockentity.NetworkBlockEntity;
import dev.vfyjxf.conduitstratus.utils.LevelHelper;
import dev.vfyjxf.conduitstratus.utils.StratusLocations;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;

public class LinearRemoteConnector extends BasicAttachableDevice {

    @SuppressWarnings("ConstantConditions")
    public static final DeviceType TYPE = new DeviceType(StratusLocations.of("linear_remote_connector"), true, LinearRemoteConnector::new);

    //TODO:Make it configurable
    private static final int MAX_DISTANCE = 32;

    /**
     * The remote node bound to this connector.
     */
    private ConduitNodeId remoteNode;
    /**
     * Bind to a remote connector pair.
     */
    private boolean bind = false;

    protected LinearRemoteConnector(DeviceType type, NetworkNode holder, Direction direction) {
        super(type, holder, direction);
        this.status = new TickStatus(10, 10);
    }


    @Override
    public void saveData(CompoundTag tag, HolderLookup.Provider registries) {
        if (remoteNode != null) {
            tag.put("remoteNode", remoteNode.toTag());
        }
    }

    @Override
    public void loadData(CompoundTag tag, HolderLookup.Provider registries) {
        if (tag.contains("remoteNode")) {
            remoteNode = ConduitNodeId.fromTag(tag.getCompound("remoteNode"));
        }
    }

    @Override
    public void tick(int ticksSinceLast) {
        if (remoteNode != null) {
            checkRemote();
        }
        LinearRemoteConnector connector = findRemoteConnector();
        if (connector != null) {
            this.bind = true;
            connector.bind = true;
            remoteNode = connector.getNode().getId();
            connector.remoteNode = getNode().getId();
        }
        if (remoteNode != null) {
            status.sleep();
        }
    }

    private void checkRemote() {
        if (remoteNode == null) return;
        ServerLevel level = getNode().getLevel();
        boolean validPos = level.dimension() == remoteNode.dimension() && level.isLoaded(remoteNode.pos());
        if (!validPos) {
            remoteNode = null;
            return;
        }
        BlockEntity blockEntity = level.getBlockEntity(remoteNode.pos());
        if (!(blockEntity instanceof NetworkBlockEntity networkEntity) || networkEntity.networkNode() == null) {
            remoteNode = null;
            return;
        }
        AttachableDevice device = networkEntity.networkNode().getDevice(direction.getOpposite());
        if (!(device instanceof LinearRemoteConnector connector)) {
            remoteNode = null;
            return;
        }
        if (connector.remoteNode == null || !connector.remoteNode.equals(getNode().getId())) {
            remoteNode = null;
        }
    }

    @Nullable
    private LinearRemoteConnector findRemoteConnector() {
        ServerLevel level = getNode().getLevel();
        if (remoteNode != null) {
            NetworkBlockEntity entity = LevelHelper.getBlockEntity(level, remoteNode.pos(), NetworkBlockEntity.class);
            if (entity != null) {
                NetworkNode node = entity.networkNode();
                if (node != null) {
                    AttachableDevice device = node.getDevice(direction.getOpposite());
                    if (device instanceof LinearRemoteConnector connector &&
                            connector.remoteNode != null &&
                            connector.remoteNode.equals(getNode().getId())
                    ) {
                        return connector;
                    }
                }
            }
        }
        BlockPos selfPos = getNode().getPos();
        Direction direction = getDirection();
        for (int i = 1; i <= MAX_DISTANCE; i++) {
            BlockPos remotePos = selfPos.relative(direction, i);
            if (!level.isLoaded(remotePos)) continue;
            NetworkBlockEntity entity = LevelHelper.getBlockEntity(level, remotePos, NetworkBlockEntity.class);
            if (entity == null) continue;
            NetworkNode node = entity.networkNode();
            if (node == null) continue;
            AttachableDevice device = node.getDevice(direction.getOpposite());
            if (!(device instanceof LinearRemoteConnector connector) || connector.bind) continue;
            return connector;
        }
        return null;
    }
}
