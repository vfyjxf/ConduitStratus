package dev.vfyjxf.conduitstratus.api.conduit.device;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import dev.vfyjxf.conduitstratus.api.conduit.DeviceType;
import dev.vfyjxf.conduitstratus.api.conduit.TickStatus;
import dev.vfyjxf.conduitstratus.api.conduit.network.Network;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

public interface Device extends IAttachmentHolder {

    DeviceType getType();

    String identifier();

    void setIdentifier(String identifier);

    default Network getNetwork() {
        return getNode().getNetwork();
    }

    NetworkNode getNode();

    @CanIgnoreReturnValue
    @Contract("_ -> this")
    Device setStatus(TickStatus status);

    /**
     * @return the working status of the device.
     */
    TickStatus getStatus();

    default boolean shouldTick(long currentTick) {
        return checkStatus(currentTick) && getStatus().shouldWork(currentTick);
    }

    default boolean sleeping() {
        return getStatus().sleeping();
    }

    default boolean working() {
        return getStatus().working();
    }

    @MustBeInvokedByOverriders
    void saveData(CompoundTag tag, HolderLookup.Provider registries);

    @MustBeInvokedByOverriders
    void loadData(CompoundTag tag, HolderLookup.Provider registries);

    /**
     * Call when {@link TickStatus#shouldWork(long)} returns true.
     *
     * @param ticksSinceLast the ticks since last tick
     */
    default void tick(int ticksSinceLast) {

    }

    default boolean checkStatus(long currentTick) {
        return true;
    }

}
