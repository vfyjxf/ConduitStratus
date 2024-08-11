package dev.vfyjxf.conduitstratus.conduit.traits;

import dev.vfyjxf.conduitstratus.api.conduit.trait.BasicTrait;
import dev.vfyjxf.conduitstratus.api.conduit.trait.ConduitTraitType;

public class ItemTrait extends BasicTrait<ItemTrait> {

    @Override
    public ConduitTraitType<ItemTrait> getType() {
        return null;
    }

    @Override
    public boolean handle() {
        return false;
    }
}
