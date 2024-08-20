package dev.vfyjxf.conduitstratus.api.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.ConduitIO;
import dev.vfyjxf.conduitstratus.api.conduit.trait.ConduitTrait;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.MutableSet;
import org.jetbrains.annotations.Contract;

//TODO:去泛化，这里没必要用泛型，我们支持不同类型的trait相互交换数据
public interface NetworkChannel {

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
