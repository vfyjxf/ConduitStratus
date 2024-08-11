package dev.vfyjxf.conduitstratus.api.conduit;

public enum ConduitIO {

    INPUT,
    OUTPUT,
    BOTH,
    NONE;

    public boolean input() {
        return this == INPUT || this == BOTH;
    }

    public boolean output() {
        return this == OUTPUT || this == BOTH;
    }

}
