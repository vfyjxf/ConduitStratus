package dev.vfyjxf.conduitstratus.api.conduit.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Managing the initialization and termination of nodes
 */
@ApiStatus.NonExtendable
public interface InitNetworkNode {

    /**
     * @return null if the dist is client or the node is not available.
     */
    @Nullable
    NetworkNode getNode();

    @Nullable
    Network getNetwork();

    boolean available();

    void destroy();

    void build(Level level, BlockEntity holder);

    void saveData(CompoundTag data);

    void loadData(CompoundTag data);

}
