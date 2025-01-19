package dev.vfyjxf.conduitstratus.conduit.traits.io;

import dev.vfyjxf.conduitstratus.api.conduit.HandleType;
import dev.vfyjxf.conduitstratus.api.conduit.HandleTypes;
import dev.vfyjxf.conduitstratus.api.conduit.TickStatus;
import dev.vfyjxf.conduitstratus.api.conduit.io.LogisticManager;
import dev.vfyjxf.conduitstratus.api.conduit.network.Network;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkChannels;
import dev.vfyjxf.conduitstratus.conduit.traits.fluid.FluidRequest;
import dev.vfyjxf.conduitstratus.conduit.traits.fluid.FluidResponse;
import dev.vfyjxf.conduitstratus.conduit.traits.fluid.FluidTransferTrait;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;

public class FluidLogisticManager implements LogisticManager<FluidTransferTrait, FluidRequest, FluidResponse> {

    @Override
    public HandleType getHandleType() {
        return HandleTypes.FLUID;
    }

    @Override
    public NetworkChannels<FluidTransferTrait> createChannels(Network network) {
        return network.createChannels(getHandleType(), FluidTransferTrait.class::isInstance);
    }

    @Override
    public void tick(Network network, NetworkChannels<FluidTransferTrait> channels, long currentTick) {
        for (var ioMap : channels.mapped()) {
            ioMap.forEach((exporter, importers) -> {
                //region check tick status
                TickStatus exporterStatus = exporter.getStatus();
                if (!exporterStatus.shouldTick(currentTick)) return;
                IFluidHandler exporterHandler = exporter.getCapability();
                if (exporterHandler == null) return;
                exporterStatus.setLastTick(currentTick);
                //endregion
                //region export
                int amountToExport = exporter.exportSpeed();
                for (int tank = 0; tank < exporterHandler.getTanks() && amountToExport > 0; tank++) {
                    FluidStack fluidInTank = exporterHandler.getFluidInTank(tank);
                    if (fluidInTank.isEmpty()) continue;//fast fail
                    FluidStack toExport = exporterHandler.drain(fluidInTank.copyWithAmount(amountToExport), FluidAction.SIMULATE);
                    if (toExport.isEmpty()) continue;//fast fail
                    FluidStack left = toExport.copy();
                    int filled = 0;
                    for (var iterator = importers.iterator(); !left.isEmpty() && iterator.hasNext(); ) {
                        var importer = iterator.next();
                        IFluidHandler importHandler = importer.getCapability();
                        if (importHandler == null) continue;
                        filled += importHandler.fill(left, FluidAction.EXECUTE);
                        left = toExport.copyWithAmount(amountToExport - filled);
                    }
                    exporterHandler.drain(filled, FluidAction.EXECUTE);
                    amountToExport -= filled;
                }
                //endregion
            });
        }

    }

    @Override
    public FluidResponse handleRequest(NetworkChannels<FluidTransferTrait> channels, FluidTransferTrait sender, FluidRequest request) {
        return FluidResponse.illegal("Not implemented");
    }

}
