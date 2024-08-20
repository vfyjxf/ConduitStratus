package dev.vfyjxf.conduitstratus.api.conduit.trait;

import dev.vfyjxf.conduitstratus.api.conduit.Conduit;
import dev.vfyjxf.conduitstratus.api.conduit.ConduitIO;
import dev.vfyjxf.conduitstratus.api.conduit.event.TraitEvent;
import dev.vfyjxf.conduitstratus.api.conduit.network.Network;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import dev.vfyjxf.conduitstratus.api.event.EventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.fluids.FluidStack;
import org.eclipse.collections.api.set.MutableSet;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * The capability create a {@link Conduit} to handle data. such as {@link ItemStack}s, {@link FluidStack}s, etc.
 */
public interface ConduitTrait<T extends ConduitTrait<T>> extends EventHandler<TraitEvent> {

    ConduitTraitType<T> getType();

    NetworkNode getNode();

    default Network getNetwork() {
        return getNode().getNetwork();
    }

    default ServerLevel getLevel() {
        return getNode().getLevel();
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

    @Nullable
    TraitConnection getConnection();

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
    default MutableSet<BlockCapability<?, @Nullable Direction>> proxyCapabilities() {
        return getType().proxyCapabilities();
    }

    /**
     * Additional checks for TraitPlugin.
     */
    default boolean perHandle() {
        return true;
    }

    boolean handle();

    default void postHandle() {

    }
}
