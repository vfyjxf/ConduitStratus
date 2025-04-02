package dev.vfyjxf.conduitstratus.api.conduit.device;

import dev.vfyjxf.conduitstratus.api.conduit.DeviceType;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface DeviceFactory {

    /**
     * @param type        The type of device to create
     * @param networkNode The network node the device is attached to
     * @param direction   The direction of this device will be attached to the network node.
     *                    It will be null if the device is not a {@link AttachableDevice}
     * @return The created device
     */
    Device create(DeviceType type, NetworkNode networkNode, @Nullable Direction direction);

}
