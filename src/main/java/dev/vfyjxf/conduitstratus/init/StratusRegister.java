package dev.vfyjxf.conduitstratus.init;

import dev.vfyjxf.conduitstratus.Constants;
import dev.vfyjxf.conduitstratus.api.StratusRegisterEvent;
import dev.vfyjxf.conduitstratus.api.conduit.HandleTypes;
import dev.vfyjxf.conduitstratus.conduit.traits.io.FluidLogisticManager;
import dev.vfyjxf.conduitstratus.conduit.traits.io.ItemLogisticManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = Constants.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class StratusRegister {

    @SubscribeEvent
    private static void onStratusRegister(StratusRegisterEvent event) {
        event.getRegistry().registerLogisticManager(HandleTypes.ITEM, new ItemLogisticManager());
        event.getRegistry().registerLogisticManager(HandleTypes.FLUID, new FluidLogisticManager());
    }
}
