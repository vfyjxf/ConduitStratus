package dev.vfyjxf.conduitstratus.api.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.ConduitIO;
import dev.vfyjxf.conduitstratus.api.conduit.trait.ConduitTrait;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.set.MutableSet;
import org.jetbrains.annotations.Contract;

public interface NetworkChannel<T extends ConduitTrait<T>> {

    ChannelColor getChannelColor();

    MutableSet<? extends ConduitTrait<T>> allTraits();

    /**
     * @return the output to input maps
     */
    MutableMap<ConduitTrait<T>, MutableList<? extends ConduitTrait<T>>> ioMaps();

    MutableList<? extends ConduitTrait<T>> getByIO(ConduitIO conduitIO);

    boolean contains(ConduitTrait<T> trait);

    @Contract("_ -> this")
    NetworkChannel<T> addTrait(ConduitTrait<T> trait);

    @Contract("_ -> this")
    NetworkChannel<T> removeTrait(ConduitTrait<T> trait);

}
