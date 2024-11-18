package dev.vfyjxf.conduitstratus.api.conduit;

import dev.vfyjxf.conduitstratus.Constants;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class StratusComponents {

    private static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Constants.MOD_ID);


    public static void init(IEventBus bus) {
        COMPONENTS.register(bus);
    }

    private StratusComponents() {
    }
}
