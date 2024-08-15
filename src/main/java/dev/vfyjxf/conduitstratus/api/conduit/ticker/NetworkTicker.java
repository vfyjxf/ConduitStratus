package dev.vfyjxf.conduitstratus.api.conduit.ticker;

import dev.vfyjxf.conduitstratus.api.conduit.network.Network;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Range;

public interface NetworkTicker {

    void tickNetwork(ServerLevel level, Network network, NetworkNode node);

    /**
     * @return the rate at which the network node/trait should tick. 1 is every tick, 20 is every second, etc.
     */
    @Range(from = 1, to = Integer.MAX_VALUE)
    default int tickRate() {
        return 20;
    }
}
