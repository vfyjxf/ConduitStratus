package dev.vfyjxf.conduitstratus.api.conduit.trait;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

public interface CapabilityConnection<CAP> extends TraitConnection {

    BlockCapability<? extends CAP, @Nullable Direction> getToken();

    CAP getCapability();

}
