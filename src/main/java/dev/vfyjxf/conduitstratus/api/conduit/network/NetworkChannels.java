package dev.vfyjxf.conduitstratus.api.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.HandleType;
import dev.vfyjxf.conduitstratus.api.conduit.TraitIO;
import dev.vfyjxf.conduitstratus.api.conduit.trait.Trait;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.MutableSet;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;

//TODO:ConduitIO为NONE时的trait是否应该被纳入频道内？
//TODO:我们该允许自我循环输入输出吗:yes
public interface NetworkChannels<TRAIT extends Trait> {

    Network getNetwork();

    HandleType getHandleType();

    boolean accept(Trait trait);

    MutableSet<? extends TRAIT> allTraits();

    /**
     * The order by {@link ChannelColor} ordinal
     */
    MutableList<ChannelColor> usedChannels();

    /**
     * @return exporter -> importers list map, inputs are order by path distance(nearest first),exporters are order by {@link Trait#priority()}
     */
    @Unmodifiable
    MutableMap<ChannelColor, @Unmodifiable Map<TRAIT, @Unmodifiable MutableList<? extends TRAIT>>> mapped();

    @Unmodifiable
    MutableList<? extends TRAIT> importerOf(TRAIT exporter);

    default MutableList<? extends TRAIT> exporterOf(TRAIT importer) {
        return exporterOf(importer, false);
    }

    /**
     * @param importer the importer
     * @return the exporter list of importer,it may include importer itself.
     */
    MutableList<? extends TRAIT> exporterOf(TRAIT importer, boolean includeImporter);

    @Unmodifiable
    MutableList<? extends TRAIT> getByIO(TraitIO traitIO);

    MutableList<? extends TRAIT> getImporters();

    MutableList<? extends TRAIT> getExporters();

    boolean contains(TRAIT trait);

    /**
     * @throws IllegalArgumentException if {@link #accept(Trait)} return false
     */
    @Contract("_ -> this")
    NetworkChannels<TRAIT> addTrait(TRAIT trait);

    @Contract("_ -> this")
    NetworkChannels<TRAIT> removeTrait(TRAIT trait);

    interface TraitIOListener {
        /**
         * @param trait        trait
         * @param io           old io
         * @param channelColor old channel color
         */
        void onTraitIOChange(Trait trait, TraitIO io, ChannelColor channelColor);
    }

}
