package dev.vfyjxf.conduitstratus.api.conduit.io;

import dev.vfyjxf.conduitstratus.api.conduit.HandleType;
import dev.vfyjxf.conduitstratus.api.conduit.network.Network;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkChannels;
import dev.vfyjxf.conduitstratus.api.conduit.trait.TransferTrait;

public interface LogisticManager<TRAIT extends TransferTrait<REQUEST, RESPONSE>, REQUEST extends IORequest, RESPONSE> {

    HandleType getHandleType();

    NetworkChannels<TRAIT> createChannels(Network network);

    /**
     * @param network     the conduit network
     * @param channels    the channels to handle
     * @param currentTick current server tick
     */
    void tickTraits(Network network, NetworkChannels<TRAIT> channels, long currentTick);

    /**
     * @param channels the channels of request type
     * @param sender   the request sender
     * @param request  the request to handle
     * @return the response of the request
     */
    RESPONSE handleRequest(NetworkChannels<TRAIT> channels, TRAIT sender, REQUEST request);

}
