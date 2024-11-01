package dev.vfyjxf.conduitstratus.api.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.ConduitIO;
import dev.vfyjxf.conduitstratus.api.conduit.HandleType;
import dev.vfyjxf.conduitstratus.api.conduit.trait.ConduitTrait;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.set.MutableSet;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Unmodifiable;

//TODO:rewrite this,add more useful methods
//TODO:ConduitIO为NONE时的trait是否应该被纳入频道内？
//TODO:我们该允许自我循环输入输出吗:yes
public interface NetworkChannels<TRAIT extends ConduitTrait> {

    Network getNetwork();

    HandleType getHandleType();

    MutableSet<? extends TRAIT> allTraits();

    /**
     * The order by {@link ChannelColor} ordinal
     */
    MutableList<ChannelColor> usedChannels();

    /**
     * @return output -> input list map, inputs are order by path distance.
     */
    @Unmodifiable
    MutableMap<ChannelColor, @Unmodifiable MutableMap<TRAIT, @Unmodifiable MutableList<? extends TRAIT>>> mapped();

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
    MutableList<? extends TRAIT> getByIO(ConduitIO conduitIO);

    MutableList<? extends TRAIT> getImporters();

    MutableList<? extends TRAIT> getExporters();

    boolean contains(TRAIT trait);

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
        void onTraitIOChange(ConduitTrait trait, ConduitIO io, ChannelColor channelColor);
    }

}
