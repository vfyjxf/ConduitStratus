package dev.vfyjxf.conduitstratus.api.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.ConduitEntity;
import dev.vfyjxf.conduitstratus.api.conduit.trait.Trait;
import dev.vfyjxf.conduitstratus.api.conduit.trait.TraitType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Unmodifiable;

import javax.annotation.Nullable;
import java.util.Collection;

@ApiStatus.NonExtendable
public interface NetworkNode {

    Network getEffectiveNetwork();

    ConduitEntity getHolder();

    default BlockPos getPos() {
        return getHolder().getPos();
    }

    default ServerLevel getLevel() {
        return (ServerLevel) getHolder().getBlockEntity().getLevel();
    }

    void addTrait(Direction direction, Trait trait);

    boolean hasTrait(TraitType type);

    /**
     * @param type the trait type
     * @return the list create the attached traits, if the type is not attached, an empty list will be returned.
     */
    @Unmodifiable
    MutableMap<Direction, ? extends Trait> getTraits(TraitType type);

    @Unmodifiable
    MutableList<? extends Trait> getTraits(Direction direction);

    @Unmodifiable
    MutableMap<Direction, MutableList<Trait>> allTraits();

    @Nullable
    <T, C> T poxyCapability(BlockCapability<T, C> capability, @Nullable C context);

    void destroy(boolean remove);

    void saveData(CompoundTag tag, HolderLookup.Provider registries);

    void loadData(CompoundTag tag, HolderLookup.Provider registries);

    void setConnectedDirections(Collection<Direction> directions);
}
