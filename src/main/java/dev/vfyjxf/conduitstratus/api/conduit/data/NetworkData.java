package dev.vfyjxf.conduitstratus.api.conduit.data;

/**
 * the data hole by network nodes.
 */
public interface NetworkData<T> {

    /**
     * @return the type create this data.
     */
    NetworkDataType<T> type();

    /**
     * @return the direct access create this data.
     * <p>
     * it doesn't fire any event.
     */
    T directAccess();

    /**
     * @return the deep copy create this data.
     */
    T copy();

    void set(T value);

    @SuppressWarnings("unchecked")
    default <C> NetworkData<C> cast() {
        return (NetworkData<C>) this;
    }

}
