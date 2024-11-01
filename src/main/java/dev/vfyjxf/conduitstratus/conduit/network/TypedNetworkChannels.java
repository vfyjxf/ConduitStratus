package dev.vfyjxf.conduitstratus.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.ConduitIO;
import dev.vfyjxf.conduitstratus.api.conduit.HandleType;
import dev.vfyjxf.conduitstratus.api.conduit.network.ChannelColor;
import dev.vfyjxf.conduitstratus.api.conduit.network.Network;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkChannels;
import dev.vfyjxf.conduitstratus.api.conduit.trait.ConduitTrait;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.FixedSizeMap;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.map.mutable.MapAdapter;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Unmodifiable;

import java.util.EnumMap;

@ApiStatus.Internal
public class TypedNetworkChannels<TRAIT extends ConduitTrait> implements NetworkChannels<TRAIT> {

    private final Network network;
    private final HandleType handleType;
    private final FixedSizeMap<ConduitIO, MutableList<TRAIT>> allTraits = Maps.fixedSize.with(
            ConduitIO.INPUT, Lists.mutable.empty(),
            ConduitIO.OUTPUT, Lists.mutable.empty(),
            ConduitIO.BOTH, Lists.mutable.empty()
    );
    /**
     * Note: 通常为少量多次的更新
     * output -> inputs
     */
    private final MutableMap<ChannelColor, MutableMap<TRAIT, MutableList<? extends TRAIT>>> mappedByChannel = MapAdapter.adapt(new EnumMap<>(ChannelColor.class));

    public TypedNetworkChannels(Network network, HandleType handleType) {
        this.network = network;
        this.handleType = handleType;
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
    public MutableSet<? extends TRAIT> allTraits() {
        return allTraits.flatCollect(MutableList::toSet).toSet();
    }

    @Override
    public MutableList<ChannelColor> usedChannels() {
        return mappedByChannel.keysView().toSortedList(Enum::compareTo);
    }

    @Override
    @Unmodifiable
    public MutableMap<ChannelColor, @Unmodifiable MutableMap<TRAIT, @Unmodifiable MutableList<? extends TRAIT>>> mapped() {
        return mappedByChannel.asUnmodifiable();
    }

    @Override
    public MutableList<? extends TRAIT> importerOf(TRAIT exporter) {
        return mappedByChannel.get(exporter.getChannel()).get(exporter).asUnmodifiable();
    }

    @Override
    public MutableList<? extends TRAIT> exporterOf(TRAIT importer, boolean includeImporter) {
        MutableList<TRAIT> exporters = Lists.mutable.empty();
        for (MutableMap<TRAIT, MutableList<? extends TRAIT>> ioMap : mappedByChannel) {
            ioMap.forEachKeyValue((exporter, importers) -> {
                if (importers.contains(importer)) {
                    exporters.add(exporter);
                }
            });
        }
        return exporters;
    }

    @Override
    public MutableList<? extends TRAIT> getByIO(ConduitIO conduitIO) {
        return allTraits.get(conduitIO).asUnmodifiable();
    }

    @Override
    public MutableList<? extends TRAIT> getImporters() {
        MutableList<TRAIT> inputs = allTraits.get(ConduitIO.INPUT);
        inputs.addAll(allTraits.get(ConduitIO.BOTH));
        return inputs;
    }

    @Override
    public MutableList<? extends TRAIT> getExporters() {
        MutableList<TRAIT> outputs = allTraits.get(ConduitIO.OUTPUT);
        outputs.addAll(allTraits.get(ConduitIO.BOTH));
        return outputs;
    }

    @Override
    public boolean contains(TRAIT trait) {
        return allTraits.valuesView().anySatisfy(t -> t.contains(trait));
    }

    @Override
    public NetworkChannels<TRAIT> addTrait(TRAIT trait) {
        if (!trait.getIO().doAny()) return this;
        //TODO:implement
        updateIOMaps(trait, false);
        return this;
    }

    @Override
    public NetworkChannels<TRAIT> removeTrait(TRAIT trait) {
        //TODO:implement
        updateIOMaps(trait, true);
        return this;
    }

    private void updateIOMaps(TRAIT trait, boolean remove) {
        //TODO:implement
    }

}
