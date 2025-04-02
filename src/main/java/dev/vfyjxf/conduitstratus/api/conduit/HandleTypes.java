package dev.vfyjxf.conduitstratus.api.conduit;

import dev.vfyjxf.conduitstratus.utils.StratusLocations;

public final class HandleTypes {

    public static final HandleType ITEM = new HandleType(StratusLocations.of("item"));
    public static final HandleType FLUID = new HandleType(StratusLocations.of("fluid"));
    public static final HandleType FE = new HandleType(StratusLocations.of("fe"));

    private HandleTypes() {
    }
}
