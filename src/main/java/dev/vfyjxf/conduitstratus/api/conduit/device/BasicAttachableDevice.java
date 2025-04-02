package dev.vfyjxf.conduitstratus.api.conduit.device;

import dev.vfyjxf.conduitstratus.api.conduit.DeviceType;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import net.minecraft.core.Direction;

/**
 *
 */
public abstract class BasicAttachableDevice extends BasicDevice implements AttachableDevice {

    protected final Direction direction;

    protected BasicAttachableDevice(DeviceType type, NetworkNode holder, Direction direction) {
        super(type, holder);
        this.direction = direction;
    }

    @Override
    public Direction getDirection() {
        return direction;
    }
}
