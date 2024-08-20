package dev.vfyjxf.conduitstratus.api.conduit.network;

public interface NetworkService<T extends NetworkService<T>> {

    NetworkServiceType<T> type();

    default boolean hasCodec() {
        return type().hasCodec();
    }

    /**
     * Merge this network with another network
     *
     * @param other the other network to merge with
     */
    void mergeWith(NetworkService<T> other);

}
