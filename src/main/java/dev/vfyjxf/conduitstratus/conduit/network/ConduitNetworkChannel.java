package dev.vfyjxf.conduitstratus.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.ConduitIO;
import dev.vfyjxf.conduitstratus.api.conduit.network.ChannelColor;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkChannel;
import dev.vfyjxf.conduitstratus.api.conduit.trait.ConduitTrait;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.FixedSizeMap;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.MutableSet;

public class ConduitNetworkChannel<T extends ConduitTrait<T>> implements NetworkChannel<T> {

    private final ChannelColor color;
    private final FixedSizeMap<ConduitIO, MutableList<ConduitTrait<T>>> allTraits = Maps.fixedSize.with(
            ConduitIO.INPUT, Lists.mutable.empty(),
            ConduitIO.OUTPUT, Lists.mutable.empty(),
            ConduitIO.BOTH, Lists.mutable.empty()
    );
    /**
     * Note: 通常为少量多次的更新
     */
    private final MutableMap<ConduitTrait<T>, MutableList<? extends ConduitTrait<T>>> mapped = Maps.mutable.empty();

    public ConduitNetworkChannel(ChannelColor color) {
        this.color = color;
    }

    @Override
    public ChannelColor getChannelColor() {
        return color;
    }

    @Override
    public MutableSet<? extends ConduitTrait<T>> allTraits() {
        return allTraits.flatCollect(MutableList::toSet).toSet();
    }

    @Override
    public MutableMap<ConduitTrait<T>, MutableList<? extends ConduitTrait<T>>> ioMaps() {
        return mapped.clone();
    }

    @Override
    public MutableList<? extends ConduitTrait<T>> getByIO(ConduitIO conduitIO) {
        return allTraits.get(conduitIO);
    }

    @Override
    public boolean contains(ConduitTrait<T> trait) {
        return allTraits.valuesView().anySatisfy(t -> t.contains(trait));
    }

    @Override
    public NetworkChannel<T> addTrait(ConduitTrait<T> trait) {
        allTraits.get(trait.getIO()).add(trait);
        updateIOMaps();
        return this;
    }

    @Override
    public NetworkChannel<T> removeTrait(ConduitTrait<T> trait) {
        allTraits.get(trait.getIO()).remove(trait);
        updateIOMaps();
        return this;
    }

    private void updateIOMaps() {
        //TODO:
    }

}
