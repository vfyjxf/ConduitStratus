package dev.vfyjxf.conduitstratus;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(value = Constants.MOD_ID, dist = Dist.CLIENT)
public class ConduitStratusClient extends ConduitStratus {
    public static final Logger logger = LoggerFactory.getLogger("ConduitStratus-Client");

    public ConduitStratusClient(IEventBus modEventBus, ModContainer modContainer) {
        super(modEventBus, modContainer);
    }
}
