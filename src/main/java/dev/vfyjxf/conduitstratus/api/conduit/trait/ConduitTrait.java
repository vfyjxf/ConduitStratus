package dev.vfyjxf.conduitstratus.api.conduit.trait;

import dev.vfyjxf.conduitstratus.api.conduit.ConduitIO;
import dev.vfyjxf.conduitstratus.api.conduit.Conduit;
import dev.vfyjxf.conduitstratus.api.conduit.event.TraitEvent;
import dev.vfyjxf.conduitstratus.api.conduit.network.Network;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import dev.vfyjxf.conduitstratus.api.event.IEventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * The capability create a {@link Conduit} to handle data. such as {@link ItemStack}s, {@link FluidStack}s, etc.
 */
public interface ConduitTrait<T extends ConduitTrait<T>> extends IEventHandler<TraitEvent> {

    ConduitTraitType<T> getType();

    NetworkNode getNode();

    @Nullable
    default Level getLevel() {
        return getNode().getLevel();
    }

    default Network getNetwork() {
        return getNode().getNetwork();
    }

    default Conduit getConduit() {
        return getNode().getConduit();
    }

    /**
     * @return the facing direction of the trait.
     */
    Direction getDirection();

    ConduitIO getIO();

    /**
     * @param conduitIO the io
     * @return this
     */
    @Contract("_ -> this")
    ConduitTrait<T> setIO(ConduitIO conduitIO);

    @Nullable
    default BlockEntity getFacing() {
        NetworkNode holder = getNode();
        Level level = holder.getLevel();
        if (level == null) return null;
        else return level.getBlockEntity(holder.getPos().relative(getDirection()));
    }

    @Nullable
    TraitConnection getConnection();

    /**
     * @return whether the trait is connectable with facing block.
     */
    default boolean connectable() {
        BlockEntity facing = getFacing();
        if (facing == null) return false;
        Level level = facing.getLevel();
        if (level == null) return false;
        BlockPos pos = facing.getBlockPos();
        Direction direction = getDirection().getOpposite();
        for (BlockCapability<?, @Nullable Direction> handleCapability : getType().handleCapabilities()) {
            if (level.getCapability(handleCapability, pos, direction) != null) {
                return true;
            }
        }
        return false;
    }

    default boolean connected() {
        return getConnection() != null;
    }

    default void disconnect() {
        TraitConnection connection = getConnection();
        if (connection != null) {
            connection.destroy();
        }
    }

    boolean perHandle();

    boolean handle();

    boolean postHandle();
}
