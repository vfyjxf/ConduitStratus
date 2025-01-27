package dev.vfyjxf.conduitstratus.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.network.Network;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public interface NetworkHolder {

    /**
     * @return null if node is invalid
     */
    @Nullable
    Network getNetwork();

    void setNetwork(Network network);

}
