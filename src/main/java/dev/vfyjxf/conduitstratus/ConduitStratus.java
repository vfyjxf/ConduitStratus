package dev.vfyjxf.conduitstratus;

import dev.vfyjxf.conduitstratus.config.Config;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ConduitStratus {
    public static final Logger logger = LoggerFactory.getLogger("ConduitStratus");

    public ConduitStratus(IEventBus modEventBus, ModContainer modContainer) {
        Config.register(modContainer);
    }
}
