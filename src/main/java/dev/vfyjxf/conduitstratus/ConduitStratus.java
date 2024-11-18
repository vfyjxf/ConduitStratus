package dev.vfyjxf.conduitstratus;

import dev.vfyjxf.conduitstratus.init.values.ModValues;
import dev.vfyjxf.conduitstratus.config.Config;
import dev.vfyjxf.conduitstratus.utils.tick.TickDispatcher;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ConduitStratus {
    public static final Logger logger = LoggerFactory.getLogger("ConduitStratus");

    public ConduitStratus(IEventBus modBus, ModContainer modContainer) {
        Config.register(modContainer);
        ModValues.register(modBus);
        TickDispatcher.instance().init();
    }
}
