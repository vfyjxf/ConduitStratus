package dev.vfyjxf.conduitstratus.api.conduit.trait;

import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

public interface CapabilityConnection<CAP> extends TraitConnection {

    BlockCapability<? extends CAP, ? extends @Nullable Object> getToken();

    @Nullable
    CAP getCapability();

}
