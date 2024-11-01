package dev.vfyjxf.conduitstratus.api.conduit.io;

import dev.vfyjxf.conduitstratus.api.conduit.ConduitIO;
import dev.vfyjxf.conduitstratus.utils.Checks;

public abstract class BasicRequest implements IORequest {

    protected final ConduitIO io;

    protected BasicRequest(ConduitIO io) {
        Checks.checkArgument(io == ConduitIO.INPUT || io == ConduitIO.OUTPUT, "IO must be INPUT or OUTPUT");
        this.io = io;
    }

    @Override
    public ConduitIO getIO() {
        return io;
    }
}
