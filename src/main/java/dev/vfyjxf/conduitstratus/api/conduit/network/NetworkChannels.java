package dev.vfyjxf.conduitstratus.api.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.ConduitIO;
import dev.vfyjxf.conduitstratus.api.conduit.HandleType;
import dev.vfyjxf.conduitstratus.api.conduit.trait.ConduitTrait;
import dev.vfyjxf.conduitstratus.conduit.network.TypedNetworkChannel;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.MutableSet;
import org.jetbrains.annotations.Contract;

public interface NetworkChannel {

    static NetworkChannel create(Network network, HandleType handleType, ChannelColor channelColor) {
        return new TypedNetworkChannel(network, handleType, channelColor);
    }

    Network getNetwork();

    HandleType getHandleType();

    ChannelColor getChannelColor();

    MutableSet<? extends ConduitTrait<?>> allTraits();

    /**
     * @return the output to input maps
     */
    MutableMap<ConduitTrait<?>, MutableList<? extends ConduitTrait<?>>> ioMaps();

    MutableList<? extends ConduitTrait<?>> getByIO(ConduitIO conduitIO);

    MutableList<? extends ConduitTrait<?>> getInputs();

    MutableList<? extends ConduitTrait<?>> getOutputs();

    boolean contains(ConduitTrait<?> trait);

    @Contract("_ -> this")
    NetworkChannel addTrait(ConduitTrait<?> trait);

    @Contract("_ -> this")
    NetworkChannel removeTrait(ConduitTrait<?> trait);

}
