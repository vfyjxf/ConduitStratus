package dev.vfyjxf.conduitstratus;


import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = Constants.MOD_ID, dist = Dist.DEDICATED_SERVER)
public class ConduitStratusServer extends ConduitStratus {
    public ConduitStratusServer(IEventBus modEventBus, ModContainer modContainer) {
        super(modEventBus, modContainer);
    }
}
