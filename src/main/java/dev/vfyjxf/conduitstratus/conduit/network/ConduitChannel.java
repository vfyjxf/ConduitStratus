package dev.vfyjxf.conduitstratus.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.ConduitIO;
import dev.vfyjxf.conduitstratus.api.conduit.network.ChannelColor;
import dev.vfyjxf.conduitstratus.api.conduit.network.IConduitChannel;
import dev.vfyjxf.conduitstratus.api.conduit.trait.IConduitTrait;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.FixedSizeMap;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.ImmutableSet;

public class ConduitChannel<T> implements IConduitChannel<T> {

    private final ChannelColor color;
    private final FixedSizeMap<ConduitIO, MutableList<IConduitTrait<T>>> allTraits = Maps.fixedSize.with(
            ConduitIO.INPUT, Lists.mutable.empty(),
            ConduitIO.OUTPUT, Lists.mutable.empty(),
            ConduitIO.BOTH, Lists.mutable.empty()
    );
    /**
     * Note: 通常为少量多次的更新
     */
    private final MutableMap<IConduitTrait<T>, MutableList<IConduitTrait<T>>> mapped = Maps.mutable.empty();

    public ConduitChannel(ChannelColor color) {
        this.color = color;
    }

    @Override
    public ChannelColor getChannelColor() {
        return color;
    }

    @Override
    public ImmutableSet<IConduitTrait<T>> allTraits() {
        return allTraits.flatCollect(i -> i).toImmutableSet();
    }

    @Override
    public ImmutableMap<IConduitTrait<T>, MutableList<IConduitTrait<T>>> ioMaps() {
        return mapped.toImmutable();
    }

    @Override
    public MutableList<IConduitTrait<T>> getByIO(ConduitIO conduitIO) {
        return allTraits.get(conduitIO);
    }

    @Override
    public boolean contains(IConduitTrait<T> trait) {
        return allTraits.valuesView().anySatisfy(t -> t.contains(trait));
    }

    @Override
    public IConduitChannel<T> addTrait(IConduitTrait<T> trait) {
        allTraits.get(trait.getIO()).add(trait);
        updateIOMaps();
        return this;
    }

    @Override
    public IConduitChannel<T> removeTrait(IConduitTrait<T> trait) {
        allTraits.get(trait.getIO()).remove(trait);
        updateIOMaps();
        return this;
    }

    private void updateIOMaps() {
        //TODO:
    }

}
