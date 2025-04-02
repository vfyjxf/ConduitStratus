package dev.vfyjxf.conduitstratus.api.conduit.network;

import dev.vfyjxf.conduitstratus.api.conduit.ConduitEntity;
import dev.vfyjxf.conduitstratus.api.conduit.DeviceType;
import dev.vfyjxf.conduitstratus.api.conduit.TraitType;
import dev.vfyjxf.conduitstratus.api.conduit.connection.ConduitNodeId;
import dev.vfyjxf.conduitstratus.api.conduit.device.AttachableDevice;
import dev.vfyjxf.conduitstratus.api.conduit.trait.Trait;
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

@ApiStatus.NonExtendable
public interface NetworkNode {

    ConduitNodeId getId();

    /**
     * @return the effective network of this node
     * @throws IllegalStateException if the network is not present or the network is invalid
     * @see #online() to check if the network is valid
     */
    Network getNetwork();

    /**
     * @return whether the node is in a valid network
     */
    boolean online();

    /**
     * @return reverse of {@link #online()}
     */
    default boolean offline() {
        return !online();
    }

    ConduitEntity getHolder();

    default BlockPos getPos() {
        return getHolder().getPos();
    }

    default ServerLevel getLevel() {
        return (ServerLevel) getHolder().getBlockEntity().getLevel();
    }

    void addDevice(Direction direction, AttachableDevice device);

    boolean hasDevice(DeviceType type);

    @Unmodifiable
    MutableMap<Direction, AttachableDevice> getDevices(DeviceType type);

    @Nullable
    AttachableDevice getDevice(Direction direction);

    @Unmodifiable
    MutableMap<Direction, AttachableDevice> allDevices();

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

}
