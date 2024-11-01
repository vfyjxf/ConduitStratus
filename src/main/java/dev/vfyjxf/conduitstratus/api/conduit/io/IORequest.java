package dev.vfyjxf.conduitstratus.api.conduit.io;

import dev.vfyjxf.conduitstratus.api.conduit.ConduitIO;
import dev.vfyjxf.conduitstratus.api.conduit.HandleType;

/**
 * A marker interface for IO requests
 */
public interface IORequest {

    HandleType type();

    /**
     * NOTE:Only support {@link ConduitIO#INPUT} or {@link ConduitIO#OUTPUT}
     *
     * @return the IO of the request
     */
    ConduitIO getIO();

    default boolean input() {
        return getIO().input();
    }

    default boolean output() {
        return getIO().output();
    }

}
