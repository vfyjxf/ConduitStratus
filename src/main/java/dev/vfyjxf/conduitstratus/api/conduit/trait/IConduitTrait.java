package dev.vfyjxf.conduitstratus.api.conduit.trait;

import dev.vfyjxf.conduitstratus.api.conduit.ConduitIO;
import dev.vfyjxf.conduitstratus.api.conduit.IConduit;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Contract;

/**
 * The capability of a {@link IConduit} to handle data. such as {@link ItemStack}s, {@link FluidStack}s, etc.
 */
public interface IConduitTrait<T> {

    ConduitTraitType<T> getType();

    IConduit getHolder();

    ConduitIO getIO();

    /**
     * @param conduitIO the io
     * @return this
     */
    @Contract("_ -> this")
    IConduitTrait<T> setIO(ConduitIO conduitIO);

    boolean handle();

}
