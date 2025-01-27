package dev.vfyjxf.conduitstratus.conduit.traits.energy;

import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import dev.vfyjxf.conduitstratus.api.conduit.trait.BasicTransferCapabilityTrait;
import dev.vfyjxf.conduitstratus.api.conduit.trait.TraitType;
import dev.vfyjxf.conduitstratus.conduit.traits.connection.CachedCapabilityTraitConnection;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public abstract class ForgeEnergyTransferTrait extends BasicTransferCapabilityTrait<IEnergyStorage, FERequest, FEResponse> {
    protected ForgeEnergyTransferTrait(
            TraitType type,
            NetworkNode holder,
            Direction direction
    ) {
        super(type, holder, direction, Capabilities.EnergyStorage.BLOCK);
        this.connection = new CachedCapabilityTraitConnection<>(
                this,
                Capabilities.EnergyStorage.BLOCK,
                direction.getOpposite()
        );
    }
}
