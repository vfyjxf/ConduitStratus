package dev.vfyjxf.conduitstratus;

import dev.vfyjxf.conduitstratus.api.data.lang.LangKeyProvider;
import net.minecraft.data.DataProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(value = Constants.MOD_ID, dist = Dist.CLIENT)
public class ConduitStratusClient extends ConduitStratus {
    public static final Logger logger = LoggerFactory.getLogger("ConduitStratus-Client");

    public ConduitStratusClient(IEventBus modEventBus, ModContainer modContainer) {
        super(modEventBus, modContainer);
        modEventBus.addListener((GatherDataEvent event) -> {
            event.getGenerator().addProvider(
                    event.includeClient(),
                    (DataProvider.Factory<DataProvider>) (output) -> new LangKeyProvider(Constants.MOD_ID, output)
            );
        });
    }


}
