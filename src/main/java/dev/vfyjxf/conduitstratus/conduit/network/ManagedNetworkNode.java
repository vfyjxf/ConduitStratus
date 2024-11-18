package dev.vfyjxf.conduitstratus.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.network.InitNetworkNode;
import dev.vfyjxf.conduitstratus.api.conduit.network.Network;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public class ManagedNetworkNode implements InitNetworkNode {

    private static final String nodeDataKey = "nodeData";

    private static class InitData {
        private Level level;
        private BlockEntity holder;
        private CompoundTag data;

        public boolean isServer() {
            return level != null && !level.isClientSide();
        }

        public ConduitNetworkNode createNode() {
            return new ConduitNetworkNode(holder);
        }
    }

    @Nullable
    private InitData initData;

    @Nullable
    private ConduitNetworkNode node;

    public ManagedNetworkNode(BlockEntity holder) {
        this.initData = new InitData();
        this.initData.holder = holder;
    }

    @Override
    public @Nullable NetworkNode getNode() {
        return node;
    }

    @Override
    public @Nullable Network getNetwork() {
        return node == null ? null : node.getNetwork();
    }

    @Override
    public boolean available() {
        return initData == null && node != null;
    }

    @Override
    public void destroy() {
        if (node != null) {
            node.destroy();
            node = null;
        }
    }

    @Override
    public void build(Level level, BlockEntity holder) {
        var initData = getInitData();
        initData.level = level;
        initData.holder = holder;
        this.initData = null;
        if (node == null && initData.isServer()) {
            if (node != null) {
                throw new IllegalStateException("The node has been built.");
            }
            var node = initData.createNode();
            if (initData.data != null) {
                node.loadData(initData.data.getCompound(nodeDataKey));
            }
            this.node = node;
            this.node.onReady();
        }
    }

    @Override
    public void saveData(CompoundTag data) {
        if (node != null) {
            var nodeData = new CompoundTag();
            node.saveData(nodeData);
            data.put(nodeDataKey, nodeData);
        }
    }

    @Override
    public void loadData(CompoundTag data) {
        if (node == null) {
            getInitData().data = data;
        } else {
            node.loadData(data.getCompound(nodeDataKey));
        }
    }

    private InitData getInitData() {
        if (initData == null) {
            throw new IllegalStateException("The node has been built.");
        }
        return initData;
    }
}
