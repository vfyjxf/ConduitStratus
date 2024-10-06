package dev.vfyjxf.conduitstratus.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.ConduitIO;
import dev.vfyjxf.conduitstratus.api.conduit.HandleType;
import dev.vfyjxf.conduitstratus.api.conduit.network.ChannelColor;
import dev.vfyjxf.conduitstratus.api.conduit.network.Network;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkChannel;
import dev.vfyjxf.conduitstratus.api.conduit.trait.ConduitTrait;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.FixedSizeMap;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.MutableSet;

public class TypedNetworkChannel implements NetworkChannel {

    private final Network network;
    private final HandleType handleType;
    private final ChannelColor color;
    private final FixedSizeMap<ConduitIO, MutableList<ConduitTrait<?>>> allTraits = Maps.fixedSize.with(
            ConduitIO.INPUT, Lists.mutable.empty(),
            ConduitIO.OUTPUT, Lists.mutable.empty(),
            ConduitIO.BOTH, Lists.mutable.empty()
    );
    /**
     * Note: 通常为少量多次的更新
     * output -> input
     */
    private final MutableMap<ConduitTrait<?>, MutableList<? extends ConduitTrait<?>>> mapped = Maps.mutable.empty();

    public TypedNetworkChannel(Network network, HandleType handleType, ChannelColor color) {
        this.network = network;
        this.handleType = handleType;
        this.color = color;
    }

    @Override
    public Network getNetwork() {
        return network;
    }

    @Override
    public HandleType getHandleType() {
        return handleType;
    }

    @Override
    public ChannelColor getChannelColor() {
        return color;
    }

    @Override
    public MutableSet<? extends ConduitTrait<?>> allTraits() {
        return allTraits.flatCollect(MutableList::toSet).toSet();
    }

    @Override
    public MutableMap<ConduitTrait<?>, MutableList<? extends ConduitTrait<?>>> ioMaps() {
        return mapped.clone();
    }

    @Override
    public MutableList<? extends ConduitTrait<?>> getByIO(ConduitIO conduitIO) {
        return allTraits.get(conduitIO).clone();
    }

    @Override
    public MutableList<? extends ConduitTrait<?>> getInputs() {
        MutableList<ConduitTrait<?>> inputs = allTraits.get(ConduitIO.INPUT);
        inputs.addAll(allTraits.get(ConduitIO.BOTH));
        inputs.distinct();
        return inputs;
    }

    @Override
    public MutableList<? extends ConduitTrait<?>> getOutputs() {
        MutableList<ConduitTrait<?>> outputs = allTraits.get(ConduitIO.OUTPUT);
        outputs.addAll(allTraits.get(ConduitIO.BOTH));
        outputs.distinct();
        return outputs;
    }

    @Override
    public boolean contains(ConduitTrait<?> trait) {
        return allTraits.valuesView().anySatisfy(t -> t.contains(trait));
    }

    @Override
    public NetworkChannel addTrait(ConduitTrait<?> trait) {
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
