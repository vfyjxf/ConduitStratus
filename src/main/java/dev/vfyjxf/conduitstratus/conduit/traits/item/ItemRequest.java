package dev.vfyjxf.conduitstratus.conduit.traits.item;

import dev.vfyjxf.conduitstratus.api.conduit.TraitIO;
import dev.vfyjxf.conduitstratus.api.conduit.HandleType;
import dev.vfyjxf.conduitstratus.api.conduit.HandleTypes;
import dev.vfyjxf.conduitstratus.api.conduit.io.BasicRequest;

public class ItemRequest extends BasicRequest {

    public ItemRequest(TraitIO io) {
        super(io);
    }


    @Override
    public HandleType type() {
        return HandleTypes.ITEM;
    }
}
