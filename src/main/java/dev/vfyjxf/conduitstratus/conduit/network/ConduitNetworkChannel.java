package dev.vfyjxf.conduitstratus.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.ConduitIO;
import dev.vfyjxf.conduitstratus.api.conduit.HandleType;
import dev.vfyjxf.conduitstratus.api.conduit.network.ChannelColor;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkChannel;
import dev.vfyjxf.conduitstratus.api.conduit.trait.ConduitTrait;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.FixedSizeMap;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.MutableSet;

public class ConduitNetworkChannel implements NetworkChannel {

    private final HandleType handleType;
    private final ChannelColor color;
    private final FixedSizeMap<ConduitIO, MutableList<ConduitTrait<ConduitTrait>>> allTraits = Maps.fixedSize.with(
            ConduitIO.INPUT, Lists.mutable.empty(),
            ConduitIO.OUTPUT, Lists.mutable.empty(),
            ConduitIO.BOTH, Lists.mutable.empty()
    );
    /**
     * Note: 通常为少量多次的更新
     * output -> input
     */
    private final MutableMap<ConduitTrait<ConduitTrait>, MutableList<? extends ConduitTrait<ConduitTrait>>> mapped = Maps.mutable.empty();

    public ConduitNetworkChannel(HandleType handleType, ChannelColor color) {
        this.handleType = handleType;
        this.color = color;
    }

    @Override
    public ChannelColor getChannelColor() {
        return color;
    }

    @Override
    public MutableSet<? extends ConduitTrait<ConduitTrait>> allTraits() {
        return allTraits.flatCollect(MutableList::toSet).toSet();
    }

    @Override
    public MutableMap<ConduitTrait<ConduitTrait>, MutableList<? extends ConduitTrait<ConduitTrait>>> ioMaps() {
        return mapped.clone();
    }

    @Override
    public MutableList<? extends ConduitTrait<ConduitTrait>> getByIO(ConduitIO conduitIO) {
        return allTraits.get(conduitIO).clone();
    }

    @Override
    public MutableList<? extends ConduitTrait<ConduitTrait>> getInputs() {
        MutableList<ConduitTrait<ConduitTrait>> inputs = allTraits.get(ConduitIO.INPUT);
        inputs.addAll(allTraits.get(ConduitIO.BOTH));
        inputs.distinct();
        return inputs;
    }

    @Override
    public MutableList<? extends ConduitTrait<ConduitTrait>> getOutputs() {
        MutableList<ConduitTrait<ConduitTrait>> outputs = allTraits.get(ConduitIO.OUTPUT);
        outputs.addAll(allTraits.get(ConduitIO.BOTH));
        outputs.distinct();
        return outputs;
    }

    @Override
    public boolean contains(ConduitTrait<ConduitTrait> trait) {
        return allTraits.valuesView().anySatisfy(t -> t.contains(trait));
    }

    @Override
    public NetworkChannel addTrait(ConduitTrait<ConduitTrait> trait) {
        if (handleType.interoperability().test(trait)) {
            allTraits.get(trait.getIO()).add(trait);
            updateIOMaps();
        }
        return this;
    }

    @Override
    public NetworkChannel removeTrait(ConduitTrait<?> trait) {
        allTraits.get(trait.getIO()).remove(trait);
        updateIOMaps();
        return this;
    }

    private void updateIOMaps() {
        //TODO:
    }

}
