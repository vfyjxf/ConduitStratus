package dev.vfyjxf.conduitstratus.api.conduit.trait;

import dev.vfyjxf.conduitstratus.api.conduit.DeviceType;
import dev.vfyjxf.conduitstratus.api.conduit.TraitType;
import dev.vfyjxf.conduitstratus.api.conduit.device.Device;
import dev.vfyjxf.conduitstratus.api.conduit.device.DeviceFactory;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import dev.vfyjxf.conduitstratus.utils.Checks;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface TraitFactory extends DeviceFactory {

    @Override
    default Device create(DeviceType type, NetworkNode networkNode, @Nullable Direction direction) {
        Checks.checkArgument(type instanceof TraitType, "type is not a TraitType");
        Checks.checkNotNull(direction, "direction");
        return create((TraitType) type, networkNode, direction);
    }

    Trait create(TraitType type, NetworkNode holder, Direction direction);

}
