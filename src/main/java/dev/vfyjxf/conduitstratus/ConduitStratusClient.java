package dev.vfyjxf.conduitstratus;

import dev.vfyjxf.conduitstratus.client.models.ConduitModel;
import dev.vfyjxf.conduitstratus.utils.Locations;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ModelEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(value = Constants.MOD_ID, dist = Dist.CLIENT)
public class ConduitStratusClient extends ConduitStratus {
    public static final Logger logger = LoggerFactory.getLogger("ConduitStratus-Client");

    public ConduitStratusClient(IEventBus modEventBus, ModContainer modContainer) {
        super(modEventBus, modContainer);
        modEventBus.addListener(this::registerModelLoader);
    }

    private void registerModelLoader(ModelEvent.RegisterGeometryLoaders event) {
        event.register(Locations.of("conduit"), new ConduitModel.Loader());
    }

}
