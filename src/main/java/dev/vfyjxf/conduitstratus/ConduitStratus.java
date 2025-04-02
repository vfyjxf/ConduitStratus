package dev.vfyjxf.conduitstratus;

import dev.vfyjxf.conduitstratus.api.StratusRegisterEvent;
import dev.vfyjxf.conduitstratus.api.StratusRegistries;
import dev.vfyjxf.conduitstratus.api.conduit.connection.ConnectionCalculation;
import dev.vfyjxf.conduitstratus.config.Config;
import dev.vfyjxf.conduitstratus.init.StratusRegistryImpl;
import dev.vfyjxf.conduitstratus.init.values.ModValues;
import dev.vfyjxf.conduitstratus.utils.tick.TickDispatcher;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ConduitStratus {
    public static final Logger logger = LoggerFactory.getLogger("ConduitStratus");

    public ConduitStratus(IEventBus modBus, ModContainer modContainer) {
        Config.register(modContainer);
        ModValues.register(modBus);
        TickDispatcher.instance().init();
        ConnectionCalculation.getInstance().init();
        modBus.addListener(this::onCommonSetupEvent);
        modBus.addListener(this::onNewRegistry);
    }

    private void onCommonSetupEvent(FMLCommonSetupEvent event) {
        event.enqueueWork(this::init);
    }

    private void onNewRegistry(NewRegistryEvent event) {
        event.register(StratusRegistries.DEVICE_TYPE_REGISTRY);
    }

    private void init() {
        logger.info("Registering Stratus Registry");
        ModLoader.postEventWrapContainerInModOrder(new StratusRegisterEvent(StratusRegistryImpl.INSTANCE));
        logger.info("Stratus Registry Registered");
    }
}
