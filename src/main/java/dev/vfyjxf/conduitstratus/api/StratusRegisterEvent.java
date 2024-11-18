package dev.vfyjxf.conduitstratus.api;

import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

public class StratusRegisterEvent extends Event implements IModBusEvent {

    private final StratusRegistry registry;

    @ApiStatus.Internal
    public StratusRegisterEvent(StratusRegistry registry) {
        this.registry = registry;
    }

    public StratusRegistry getRegistry() {
        return registry;
    }
}
