package dev.vfyjxf.conduitstratus.conduit.traits.energy;

import dev.vfyjxf.conduitstratus.api.conduit.TraitIO;
import dev.vfyjxf.conduitstratus.api.conduit.HandleType;
import dev.vfyjxf.conduitstratus.api.conduit.HandleTypes;
import dev.vfyjxf.conduitstratus.api.conduit.io.BasicRequest;

public class FERequest extends BasicRequest {

    protected FERequest(TraitIO io) {
        super(io);
    }

    @Override
    public HandleType type() {
        return HandleTypes.FE;
    }
}
