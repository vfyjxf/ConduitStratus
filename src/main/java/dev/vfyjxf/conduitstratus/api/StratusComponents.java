package dev.vfyjxf.conduitstratus.api;

import dev.vfyjxf.conduitstratus.StratusConstants;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class StratusComponents {

    private static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, StratusConstants.MOD_ID);


    public static void init(IEventBus bus) {
        COMPONENTS.register(bus);
    }

    private StratusComponents() {
    }
}
