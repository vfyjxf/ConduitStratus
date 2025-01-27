package dev.vfyjxf.conduitstratus.api.conduit.trait;

import dev.vfyjxf.conduitstratus.api.conduit.TraitIO;
import dev.vfyjxf.conduitstratus.api.conduit.TickStatus;
import dev.vfyjxf.conduitstratus.api.conduit.io.IORequest;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

public abstract class BasicTransferCapabilityTrait<CAP, REQUEST extends IORequest, RESPONSE>
        extends BasicCapabilityTrait<CAP>
        implements CapabilityTrait<CAP>, TransferTrait<REQUEST, RESPONSE> {

    protected BasicTransferCapabilityTrait(
            TraitType type,
            NetworkNode holder,
            Direction direction,
            BlockCapability<? extends CAP, @Nullable Direction> token
    ) {
        super(type, holder, direction, token);
    }

    @Override
    public BasicTransferCapabilityTrait<CAP, REQUEST, RESPONSE> setStatus(TickStatus status) {
        super.setStatus(status);
        return this;
    }

    @Override
    public BasicTransferCapabilityTrait<CAP, REQUEST, RESPONSE> setIO(TraitIO traitIO) {
        super.setIO(traitIO);
        return this;
    }
}
