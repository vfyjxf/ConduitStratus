package dev.vfyjxf.conduitstratus;

import dev.vfyjxf.conduitstratus.api.StratusRegisterEvent;
import dev.vfyjxf.conduitstratus.config.Config;
import dev.vfyjxf.conduitstratus.init.StratusRegistryImpl;
import dev.vfyjxf.conduitstratus.init.values.ModValues;
import dev.vfyjxf.conduitstratus.utils.tick.TickDispatcher;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ConduitStratus {
    public static final Logger logger = LoggerFactory.getLogger("ConduitStratus");

    public ConduitStratus(IEventBus modBus, ModContainer modContainer) {
        Config.register(modContainer);
        ModValues.register(modBus);
        TickDispatcher.instance().init();
        modBus.addListener(this::onCommonSetupEvent);
    }

    private void onCommonSetupEvent(FMLCommonSetupEvent event) {
        event.enqueueWork(this::init);
    }

    private void init() {
        logger.info("Registering Stratus Registry");
        ModLoader.postEventWrapContainerInModOrder(new StratusRegisterEvent(StratusRegistryImpl.INSTANCE));
        logger.info("Stratus Registry Registered");
    }
}
