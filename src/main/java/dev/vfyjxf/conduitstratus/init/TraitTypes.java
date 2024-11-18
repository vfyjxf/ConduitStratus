package dev.vfyjxf.conduitstratus.init;

import dev.vfyjxf.conduitstratus.api.conduit.HandleTypes;
import dev.vfyjxf.conduitstratus.api.conduit.trait.TraitType;
import dev.vfyjxf.conduitstratus.conduit.traits.item.ItemTrait;

import static dev.vfyjxf.conduitstratus.utils.Locations.of;

public final class TraitTypes {

    public static final TraitType ITEM = new TraitType(of("item"), HandleTypes.ITEM, ((type, holder, direction) -> new ItemTrait(holder, direction)));

    private TraitTypes() {
    }
}
