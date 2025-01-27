package dev.vfyjxf.conduitstratus.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.network.BaseNetwork;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public interface BaseNetworkHolder {
    @Nullable
    BaseNetwork getNetwork();

    void setNetwork(@Nullable BaseNetwork network);

}
