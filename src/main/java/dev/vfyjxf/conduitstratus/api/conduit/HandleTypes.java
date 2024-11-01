package dev.vfyjxf.conduitstratus.api.conduit;

import dev.vfyjxf.conduitstratus.utils.Locations;

public final class HandleTypes {

    public static final HandleType ITEM = new HandleType(Locations.create("item"));
    public static final HandleType FLUID = new HandleType(Locations.create("fluid"));
    public static final HandleType FE = new HandleType(Locations.create("fe"));

    private HandleTypes() {
    }
}
