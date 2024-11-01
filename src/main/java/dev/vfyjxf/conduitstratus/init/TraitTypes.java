package dev.vfyjxf.conduitstratus.init;

import dev.vfyjxf.conduitstratus.api.conduit.HandleTypes;
import dev.vfyjxf.conduitstratus.api.conduit.trait.TraitType;
import dev.vfyjxf.conduitstratus.utils.Locations;

public final class TraitTypes {

    public static final TraitType ITEM = new TraitType(Locations.create("item"), HandleTypes.ITEM);

    private TraitTypes() {
    }
}
