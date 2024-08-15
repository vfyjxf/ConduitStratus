package dev.vfyjxf.conduitstratus.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.network.Network;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface ConduitNetworkHolder {

    Network getNetwork();

    void setNetwork(Network network);

}
