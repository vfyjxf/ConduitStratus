package dev.vfyjxf.conduitstratus.utils;

import dev.vfyjxf.conduitstratus.StratusConstants;
import dev.vfyjxf.conduitstratus.debug.DebugPackage;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = StratusConstants.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class Networking {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1").executesOn(HandlerThread.MAIN);
        registrar.playToClient(
                DebugPackage.TYPE,
                DebugPackage.STREAM_CODEC,
                DebugPackage::handle
        );
    }
}
