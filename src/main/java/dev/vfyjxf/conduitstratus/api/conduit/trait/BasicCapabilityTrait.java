package dev.vfyjxf.conduitstratus.api.conduit.trait;

import dev.vfyjxf.conduitstratus.api.conduit.TraitIO;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

public abstract class BasicCapabilityTrait<CAP>
        extends BasicTrait<CapabilityConnection<CAP>>
        implements CapabilityTrait<CAP> {

    protected final BlockCapability<? extends CAP, @Nullable Direction> token;

    protected BasicCapabilityTrait(
            TraitType type,
            NetworkNode holder,
            Direction direction,
            BlockCapability<? extends CAP, @Nullable Direction> token
    ) {
        super(type, holder, direction);
        this.token = token;
    }

    @Override
    public BasicCapabilityTrait<CAP> setStatus(TraitStatus status) {
        super.setStatus(status);
        return this;
    }

    @Override
    public BasicCapabilityTrait<CAP> setIO(TraitIO traitIO) {
        super.setIO(traitIO);
        return this;
    }

    @Override
    public BlockCapability<? extends CAP, @Nullable Direction> getToken() {
        return token;
    }

}
