package dev.vfyjxf.conduitstratus.api.conduit.device;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import dev.vfyjxf.conduitstratus.api.conduit.TickStatus;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import dev.vfyjxf.conduitstratus.api.conduit.trait.Trait;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

public interface NetworkDevice {

    NetworkNode getNode();

    String identifier();

    @CanIgnoreReturnValue
    @Contract("_ -> this")
    Trait setStatus(TickStatus status);

    TickStatus getStatus();

    @MustBeInvokedByOverriders
    default void saveData(CompoundTag tag, HolderLookup.Provider registries) {
    }

    @MustBeInvokedByOverriders
    default void loadData(CompoundTag tag, HolderLookup.Provider registries) {

    }

}
