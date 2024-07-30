package dev.vfyjxf.conduitstratus.api.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.ConduitIO;
import dev.vfyjxf.conduitstratus.api.conduit.trait.IConduitTrait;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.set.ImmutableSet;
import org.jetbrains.annotations.Contract;

public interface IConduitChannel<T> {

    ChannelColor getChannelColor();

    ImmutableSet<IConduitTrait<T>> allTraits();

    /**
     * @return the output to input maps
     */
    ImmutableMap<IConduitTrait<T>, MutableList<IConduitTrait<T>>> ioMaps();

    MutableList<IConduitTrait<T>> getByIO(ConduitIO conduitIO);

    boolean contains(IConduitTrait<T> trait);

    @Contract("_ -> this")
    IConduitChannel<T> addTrait(IConduitTrait<T> trait);

    @Contract("_ -> this")
    IConduitChannel<T> removeTrait(IConduitTrait<T> trait);

}
