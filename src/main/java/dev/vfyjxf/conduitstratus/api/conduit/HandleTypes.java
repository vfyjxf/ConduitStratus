package dev.vfyjxf.conduitstratus.api.conduit;

import dev.vfyjxf.conduitstratus.utils.Locations;

public final class HandleTypes {

    public static final HandleType ITEM = new HandleType(Locations.of("item"));
    public static final HandleType FLUID = new HandleType(Locations.of("fluid"));
    public static final HandleType FE = new HandleType(Locations.of("fe"));

    private HandleTypes() {
    }
}
