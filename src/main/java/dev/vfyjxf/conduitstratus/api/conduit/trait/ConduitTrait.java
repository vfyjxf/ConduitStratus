package dev.vfyjxf.conduitstratus.api.conduit.trait;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import dev.vfyjxf.conduitstratus.api.conduit.Conduit;
import dev.vfyjxf.conduitstratus.api.conduit.ConduitIO;
import dev.vfyjxf.conduitstratus.api.conduit.HandleType;
import dev.vfyjxf.conduitstratus.api.conduit.event.TraitEvent;
import dev.vfyjxf.conduitstratus.api.conduit.network.ChannelColor;
import dev.vfyjxf.conduitstratus.api.conduit.network.Network;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import dev.vfyjxf.conduitstratus.api.event.EventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.map.MutableMap;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

/**
 * {@link ConduitTrait} defines the behavior of the interaction between the conduit and the block.
 */
//TODO:Plugin System
//TODO:Decide whether to impl data attachment system
public interface ConduitTrait extends EventHandler<TraitEvent> {

    TraitType getType();

    default HandleType getHandleType() {
        return getType().handleType();
    }

    NetworkNode getNode();

    TraitStatus getStatus();

    @CanIgnoreReturnValue
    @Contract("_ -> this")
    ConduitTrait setStatus(TraitStatus status);

    default boolean sleeping() {
        return getStatus().sleeping();
    }

    default boolean working() {
        return getStatus().working();
    }

    default Network getNetwork() {
        return getNode().getNetwork();
    }

    default ServerLevel getLevel() {
        return getNode().getLevel();
    }

    /**
     * @return the facing direction of the trait.
     */
    Direction getDirection();

    /**
     * For types that are used purely for connectivity (e.g., ae networks), we consider all TraitIOs to be BOTH.
     */
    ConduitIO getIO();

    default ChannelColor getChannel() {
        return ChannelColor.RED;
    }

    int priority();

    @CanIgnoreReturnValue
    @Contract("_ -> this")
    ConduitTrait setPriority(@Range(from = 0, to = Integer.MAX_VALUE) int priority);

    @MustBeInvokedByOverriders
    default void saveData(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putString("io", getIO().toString());
    }

    @MustBeInvokedByOverriders
    default void loadData(CompoundTag tag, HolderLookup.Provider registries) {

    }

    /**
     * @param conduitIO the io
     * @return this
     */
    @CanIgnoreReturnValue
    @Contract("_ -> this")
    ConduitTrait setIO(ConduitIO conduitIO);

    @Nullable
    default BlockEntity getFacing() {
        NetworkNode node = getNode();
        Level level = node.getLevel();
        BlockPos target = node.getPos().relative(getDirection());
        if (level.isLoaded(target)) {
            return level.getBlockEntity(target);
        } else return null;
    }

    default BlockPos getFacingPos() {
        return getNode().getPos().relative(getDirection());
    }

    default boolean attachable(Conduit conduit) {
        return true;
    }

    @Nullable
    TraitConnection getConnection();

    default boolean injectable() {
        return getIO().input() && getConnection() != null;
    }

    default boolean extractable() {
        return getIO().output() && getConnection() != null;
    }

    /**
     * @return whether the trait is connectable with facing block.
     */
    boolean connectable();

    default boolean connected() {
        return getConnection() != null;
    }

    default void disconnect() {
        TraitConnection connection = getConnection();
        if (connection != null) {
            connection.destroy();
        }
    }

    /**
     * @return the proxy capabilities of the trait.
     */
    //TODO:这是否必要？
    default MutableMap<BlockCapability<?, ?>, ?> proxyCapabilities() {
        return Maps.mutable.empty();
    }

    //TODO:更好的io交互api设计
    //1.要保证个体的独立性，允许个体拒绝来自网络的io请求
    //2.尊重网络的权利，允许网络管理整个网络的io调度
    //3.允许个体的特权，个体可以强制进行某些io推送，但其他个体可以拒绝这些推送，网络也可以拒绝这些推送

}
