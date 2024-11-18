package dev.vfyjxf.conduitstratus.api.conduit.io;

import dev.vfyjxf.conduitstratus.api.conduit.TraitIO;
import dev.vfyjxf.conduitstratus.utils.Checks;

public abstract class BasicRequest implements IORequest {

    protected final TraitIO io;

    protected BasicRequest(TraitIO io) {
        Checks.checkArgument(io == TraitIO.INPUT || io == TraitIO.OUTPUT, "IO must be INPUT or OUTPUT");
        this.io = io;
    }

    @Override
    public TraitIO getIO() {
        return io;
    }
}
