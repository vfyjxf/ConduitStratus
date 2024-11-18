package dev.vfyjxf.conduitstratus.api.conduit.io;

import dev.vfyjxf.conduitstratus.api.conduit.TraitIO;
import dev.vfyjxf.conduitstratus.api.conduit.HandleType;

/**
 * A marker interface for IO requests
 */
public interface IORequest {

    HandleType type();

    /**
     * NOTE:Only support {@link TraitIO#INPUT} or {@link TraitIO#OUTPUT}
     *
     * @return the IO of the request
     */
    TraitIO getIO();

    default boolean input() {
        return getIO().input();
    }

    default boolean output() {
        return getIO().output();
    }

}
