package dev.vfyjxf.conduitstratus.api.conduit;

public enum TraitIO {

    INPUT,
    OUTPUT,
    BOTH,
    /**
     * This is a special value that means no IO.
     * It's really equivalent to null, it's just added for null safe, and when Trait IO is set to NONE,
     * it'll be removed from the network
     */
    NONE;

    public boolean shouldHandle(TraitIO io) {
        return io != NONE && (this == io || this == BOTH);
    }

    public boolean doAny() {
        return this != NONE;
    }

    public boolean input() {
        return this == INPUT || this == BOTH;
    }

    public boolean output() {
        return this == OUTPUT || this == BOTH;
    }

}
