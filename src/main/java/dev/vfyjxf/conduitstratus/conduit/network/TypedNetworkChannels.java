package dev.vfyjxf.conduitstratus.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.HandleType;
import dev.vfyjxf.conduitstratus.api.conduit.TraitIO;
import dev.vfyjxf.conduitstratus.api.conduit.network.ChannelColor;
import dev.vfyjxf.conduitstratus.api.conduit.network.Network;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkChannels;
import dev.vfyjxf.conduitstratus.api.conduit.trait.Trait;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.FixedSizeMap;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.map.mutable.MapAdapter;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Predicate;

@ApiStatus.Internal
//TODO:Path calculation
public class TypedNetworkChannels<TRAIT extends Trait> implements NetworkChannels<TRAIT> {

    private final Network network;
    private final HandleType handleType;
    private final Predicate<Trait> traitPredicate;
    private final FixedSizeMap<TraitIO, MutableList<TRAIT>> allTraits = Maps.fixedSize.with(
            TraitIO.INPUT, Lists.mutable.empty(),
            TraitIO.OUTPUT, Lists.mutable.empty(),
            TraitIO.BOTH, Lists.mutable.empty()
    );
    /**
     * Note: 通常为少量多次的更新
     * output -> inputs
     * TODO:use  {@link it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap}
     */
    private final MutableMap<ChannelColor, @Unmodifiable Map<TRAIT, MutableList<? extends TRAIT>>> mappedByChannel = MapAdapter.adapt(new EnumMap<>(ChannelColor.class));

    private MutableList<TraitInfo<TRAIT>> traitsToAdd = Lists.mutable.empty();

    public TypedNetworkChannels(Network network, HandleType handleType, Predicate<Trait> traitPredicate) {
        this.network = network;
        this.handleType = handleType;
        this.traitPredicate = traitPredicate;
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
    public boolean accept(Trait trait) {
        return traitPredicate.test(trait);
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
    public MutableMap<ChannelColor, @Unmodifiable Map<TRAIT, @Unmodifiable MutableList<? extends TRAIT>>> mapped() {
        return mappedByChannel.asUnmodifiable();
    }

    @Override
    public MutableList<? extends TRAIT> importerOf(TRAIT exporter) {
        return mappedByChannel.get(exporter.getChannel()).get(exporter).asUnmodifiable();
    }

    @Override
    public MutableList<? extends TRAIT> exporterOf(TRAIT importer, boolean includeImporter) {
        //todo:implement
        MutableList<TRAIT> exporters = Lists.mutable.empty();
        for (var ioMap : mappedByChannel) {
            ioMap.forEach((exporter, importers) -> {
                if (importers.contains(importer)) {
                    exporters.add(exporter);
                }
            });
        }
        return exporters;
    }

    @Override
    public MutableList<? extends TRAIT> getByIO(TraitIO traitIO) {
        return allTraits.get(traitIO).asUnmodifiable();
    }

    @Override
    public MutableList<? extends TRAIT> getImporters() {
        MutableList<TRAIT> inputs = allTraits.get(TraitIO.INPUT);
        inputs.addAll(allTraits.get(TraitIO.BOTH));
        return inputs;
    }

    @Override
    public MutableList<? extends TRAIT> getExporters() {
        MutableList<TRAIT> outputs = allTraits.get(TraitIO.OUTPUT);
        outputs.addAll(allTraits.get(TraitIO.BOTH));
        return outputs;
    }

    @Override
    public boolean contains(TRAIT trait) {
        return allTraits.valuesView().anySatisfy(t -> t.contains(trait));
    }

    @Override
    public NetworkChannels<TRAIT> addTrait(TRAIT trait) {
        if (!accept(trait)) {
            throw new IllegalArgumentException("The trait: " + trait + "of type:" + trait.getHandleType() + " is not accepted by the channel: " + trait);
        }
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

    private record TraitInfo<TRAIT extends Trait>(
            TRAIT trait,
            @Nullable ChannelColor oldChannel, ChannelColor nextChannel,
            TraitIO oldIO, TraitIO nextIO
    ) {

    }

}
