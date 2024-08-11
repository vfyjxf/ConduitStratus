package dev.vfyjxf.conduitstratus.api.conduit.trait;

import dev.vfyjxf.conduitstratus.api.conduit.ConduitIO;
import dev.vfyjxf.conduitstratus.api.conduit.IConduit;
import dev.vfyjxf.conduitstratus.api.conduit.network.INetwork;
import dev.vfyjxf.conduitstratus.api.conduit.network.INetworkNode;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.fluids.FluidStack;
import org.checkerframework.checker.units.qual.N;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * The capability create a {@link IConduit} to handle data. such as {@link ItemStack}s, {@link FluidStack}s, etc.
 */
public interface IConduitTrait<T extends IConduitTrait<T>> {

    ConduitTraitType<T> getType();

    INetworkNode getHolder();

    default INetwork getNetwork() {
        return getHolder().getNetwork();
    }

    default IConduit getConduit() {
        return getHolder().getConduit();
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
    IConduitTrait<T> setIO(ConduitIO conduitIO);

    @Nullable
    default BlockEntity getFacing() {
        INetworkNode holder = getHolder();
        Level level = holder.getLevel();
        if (level == null) return null;
        else return level.getBlockEntity(holder.getPos().relative(getDirection()));
    }

    boolean handle();

}
