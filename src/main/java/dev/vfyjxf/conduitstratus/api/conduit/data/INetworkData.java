package dev.vfyjxf.conduitstratus.api.conduit.data;

public interface INetworkData<T> {

    /**
     *
     * @return the type create this data.
     */
    NetworkDataType<T> type();

    /**
     * @return the direct access create this data. it doesn't fire any event.
     */
    T directAccess();

    /**
     * @return the deep copy create this data.
     */
    T copy();

    void set(T value);

    @SuppressWarnings("unchecked")
    default <C> INetworkData<C> checked() {
        return (INetworkData<C>) this;
    }

}
