package dev.vfyjxf.conduitstratus.conduit.traits.fluid;

import dev.vfyjxf.conduitstratus.api.conduit.ConduitIO;
import dev.vfyjxf.conduitstratus.api.conduit.HandleType;
import dev.vfyjxf.conduitstratus.api.conduit.io.BasicRequest;
import dev.vfyjxf.conduitstratus.api.conduit.HandleTypes;

public class FluidRequest extends BasicRequest {

    protected FluidRequest(ConduitIO io) {
        super(io);
    }

    @Override
    public HandleType type() {
        return HandleTypes.FLUID;
    }
}
