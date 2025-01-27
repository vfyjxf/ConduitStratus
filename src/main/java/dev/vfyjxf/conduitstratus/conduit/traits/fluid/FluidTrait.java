package dev.vfyjxf.conduitstratus.conduit.traits.fluid;

import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import dev.vfyjxf.conduitstratus.api.conduit.trait.BasicTransferCapabilityTrait;
import dev.vfyjxf.conduitstratus.api.conduit.trait.PoxyTrait;
import dev.vfyjxf.conduitstratus.init.TraitTypes;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public class FluidTrait extends BasicTransferCapabilityTrait<IFluidHandler, FluidRequest, FluidResponse> implements PoxyTrait<IFluidHandler> {
    public FluidTrait(NetworkNode holder, Direction direction) {
        super(TraitTypes.FLUID, holder, direction, Capabilities.FluidHandler.BLOCK);
    }

    @Override
    public boolean hasRequest() {
        return false;
    }

    @Override
    public FluidRequest sendRequest() {
        return null;
    }

    @Override
    public FluidResponse handleRequest(FluidRequest request) {
        return null;
    }

    @Override
    public BlockCapability<IFluidHandler, @Nullable Direction> getPoxyToken() {
        return Capabilities.FluidHandler.BLOCK;
    }

    @Override
    public @Nullable IFluidHandler poxyCapability() {
        return null;
    }
}
