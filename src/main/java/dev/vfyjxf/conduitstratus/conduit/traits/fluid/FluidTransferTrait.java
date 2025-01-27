package dev.vfyjxf.conduitstratus.conduit.traits.fluid;

import dev.vfyjxf.conduitstratus.api.conduit.TickStatus;
import dev.vfyjxf.conduitstratus.api.conduit.TraitIO;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import dev.vfyjxf.conduitstratus.api.conduit.trait.BasicTransferCapabilityTrait;
import dev.vfyjxf.conduitstratus.api.conduit.trait.TraitType;
import dev.vfyjxf.conduitstratus.conduit.traits.connection.CachedCapabilityTraitConnection;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public abstract class FluidTransferTrait extends BasicTransferCapabilityTrait<IFluidHandler, FluidRequest, FluidResponse> {

    /**
     * The speed at which this trait can export fluids.
     * <p>
     * Unit: mB/t
     */
    private int exportSpeed = 200;

    protected FluidTransferTrait(
            TraitType type,
            NetworkNode holder,
            Direction direction
    ) {
        super(type, holder, direction, Capabilities.FluidHandler.BLOCK);
        this.connection = new CachedCapabilityTraitConnection<>(
                this,
                Capabilities.FluidHandler.BLOCK,
                direction.getOpposite()
        );
    }

    public int exportSpeed() {
        return exportSpeed;
    }

    public void setExportSpeed(int exportSpeed) {
        this.exportSpeed = exportSpeed;
    }

    @Override
    public FluidTransferTrait setStatus(TickStatus status) {
        super.setStatus(status);
        return this;
    }

    @Override
    public FluidTransferTrait setIO(TraitIO traitIO) {
        super.setIO(traitIO);
        return this;
    }

    @Override
    public FluidTransferTrait setPriority(int priority) {
        super.setPriority(priority);
        return this;
    }
}
