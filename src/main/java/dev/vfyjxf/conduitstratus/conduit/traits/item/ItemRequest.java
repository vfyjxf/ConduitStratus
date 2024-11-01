package dev.vfyjxf.conduitstratus.conduit.traits.item;

import dev.vfyjxf.conduitstratus.api.conduit.ConduitIO;
import dev.vfyjxf.conduitstratus.api.conduit.HandleType;
import dev.vfyjxf.conduitstratus.api.conduit.io.BasicRequest;
import dev.vfyjxf.conduitstratus.api.conduit.HandleTypes;

public class ItemRequest extends BasicRequest {

    public ItemRequest(ConduitIO io) {
        super(io);
    }


    @Override
    public HandleType type() {
        return HandleTypes.ITEM;
    }
}
