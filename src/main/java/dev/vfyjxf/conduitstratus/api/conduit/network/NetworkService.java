package dev.vfyjxf.conduitstratus.api.conduit.network;

public interface NetworkService<T extends NetworkService<T>> {

    /**
     * Merge this network with another network
     * @param other the other network to merge with
     */
    void mergeWith(NetworkService<T> other);

}
