package dev.vfyjxf.conduitstratus.api.conduit.trait;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import dev.vfyjxf.conduitstratus.api.conduit.ConduitIO;
import dev.vfyjxf.conduitstratus.api.conduit.IConduit;
import dev.vfyjxf.conduitstratus.api.conduit.ITypeDefinition;
import org.jetbrains.annotations.Contract;

/**
 * The ability of handling a specific type of data.
 */
public interface IConduitTrait<T> {

    IConduit getHost();

    ConduitIO getIO();

    /**
     * @param conduitIO the io
     * @return this
     */
    @CanIgnoreReturnValue
    @Contract("_ -> this")
    IConduitTrait<T> setIO(ConduitIO conduitIO);

    ITypeDefinition<T> handleType();

    default boolean support(Class<?> type) {
        return handleType().support(type);
    }

}
