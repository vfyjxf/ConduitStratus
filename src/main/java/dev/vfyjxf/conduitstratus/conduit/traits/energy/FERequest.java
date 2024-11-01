package dev.vfyjxf.conduitstratus.conduit.traits.energy;

import dev.vfyjxf.conduitstratus.api.conduit.ConduitIO;
import dev.vfyjxf.conduitstratus.api.conduit.HandleType;
import dev.vfyjxf.conduitstratus.api.conduit.io.BasicRequest;
import dev.vfyjxf.conduitstratus.api.conduit.HandleTypes;

public class FERequest extends BasicRequest {

    protected FERequest(ConduitIO io) {
        super(io);
    }

    @Override
    public HandleType type() {
        return HandleTypes.FE;
    }
}
