package dev.vfyjxf.conduitstratus.api.conduit.trait;

import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

/**
 * A trait that can be used to proxy a capability.
 */
public interface PoxyTrait<CAP> {

    BlockCapability<CAP, ?> getPoxyToken();

    @Nullable CAP poxyCapability();

}
