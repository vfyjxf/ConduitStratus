package dev.vfyjxf.conduitstratus.conduit.capability;

import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.map.MutableMap;
import org.jetbrains.annotations.Nullable;

public class PoxyCapabilityContainer {

    private final MutableMap<BlockCapability<?, ?>, Object> capabilities = Maps.mutable.withInitialCapacity(2);


    public <CAP> CAP getIfAbsentPut(BlockCapability<CAP, ?> capability, CAP value) {
        this.capabilities.getIfAbsentPut(capability, value);
        return value;
    }

    public <CAP> CAP getIfAbsentPut(BlockCapability<CAP, ?> capability, BlockCapabilityCache<CAP, ?> value) {
        this.capabilities.getIfAbsentPut(capability, value);
        return value.getCapability();
    }

    public <CAP> CAP put(BlockCapability<CAP, ?> capability, BlockCapabilityCache<CAP, ?> value) {
        this.capabilities.put(capability, value);
        return value.getCapability();
    }

    @SuppressWarnings("unchecked")
    public <CAP> CAP put(BlockCapability<CAP, ?> capability, CAP value) {
        return (CAP) this.capabilities.put(capability, value);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <CAP> CAP get(BlockCapability<CAP, ?> capability) {
        Object cap = this.capabilities.get(capability);
        if (cap instanceof BlockCapabilityCache<?, ?> cache) {
            return (CAP) cache.getCapability();
        } else {
            return (CAP) cap;
        }
    }

}
