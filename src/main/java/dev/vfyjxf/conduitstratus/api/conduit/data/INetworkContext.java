package dev.vfyjxf.conduitstratus.api.conduit.data;

import org.jetbrains.annotations.Nullable;

public interface INetworkContext<T> {

    NetworkContextType<T> type();

    /**
     * @return the direct access create this data. it doesn't fire any event.
     */
    T directAccess();

    T mergeWith(T other);

    @Nullable
    T copy();

    void set(T value);

    default <C> INetworkContext<C> cast() {
        return (INetworkContext<C>) this;
    }

}
