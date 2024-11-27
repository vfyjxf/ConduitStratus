package dev.vfyjxf.conduitstratus.api.conduit.trait;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import dev.vfyjxf.cloudlib.api.event.EventHandler;
import dev.vfyjxf.conduitstratus.api.conduit.Conduit;
import dev.vfyjxf.conduitstratus.api.conduit.HandleType;
import dev.vfyjxf.conduitstratus.api.conduit.TraitIO;
import dev.vfyjxf.conduitstratus.api.conduit.data.DataAttachable;
import dev.vfyjxf.conduitstratus.api.conduit.event.TraitEvent;
import dev.vfyjxf.conduitstratus.api.conduit.network.ChannelColor;
import dev.vfyjxf.conduitstratus.api.conduit.network.Network;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import dev.vfyjxf.conduitstratus.utils.LevelHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

/**
 * {@link Trait} defines the behavior of the interaction between the conduit and the block.
 */
//TODO:Plugin System
//TODO:Add identifier
public interface Trait extends EventHandler<TraitEvent>, DataAttachable {

    TraitType getType();

    default HandleType getHandleType() {
        return getType().handleType();
    }

    NetworkNode getNode();

    TraitStatus getStatus();

    String identifier();

    @CanIgnoreReturnValue
    @Contract("_ -> this")
    Trait setStatus(TraitStatus status);

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
    TraitIO getIO();

    default ChannelColor getChannel() {
        return ChannelColor.RED;
    }

    @Range(from = 0, to = Integer.MAX_VALUE)
    int priority();

    @CanIgnoreReturnValue
    @Contract("_ -> this")
    Trait setPriority(@Range(from = 0, to = Integer.MAX_VALUE) int priority);

    @MustBeInvokedByOverriders
    default void saveData(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putString("io", getIO().toString());
    }

    @MustBeInvokedByOverriders
    default void loadData(CompoundTag tag, HolderLookup.Provider registries) {

    }

    /**
     * @param traitIO the io
     * @return this
     */
    @CanIgnoreReturnValue
    @Contract("_ -> this")
    Trait setIO(TraitIO traitIO);

    @Nullable
    default BlockEntity getFacing() {
        NetworkNode node = getNode();
        Level level = node.getLevel();
        BlockPos target = node.getPos().relative(getDirection());
        return LevelHelper.getBlockEntity(level, target);
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

}
