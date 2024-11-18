package dev.vfyjxf.conduitstratus.conduit.traits.fluid;

import dev.vfyjxf.conduitstratus.api.conduit.TraitIO;
import dev.vfyjxf.conduitstratus.api.conduit.HandleType;
import dev.vfyjxf.conduitstratus.api.conduit.HandleTypes;
import dev.vfyjxf.conduitstratus.api.conduit.io.BasicRequest;

public class FluidRequest extends BasicRequest {

    protected FluidRequest(TraitIO io) {
        super(io);
    }

    @Override
    public HandleType type() {
        return HandleTypes.FLUID;
    }
}
