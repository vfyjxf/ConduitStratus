package dev.vfyjxf.conduitstratus.datagen;

import dev.vfyjxf.conduitstratus.Constants;
import dev.vfyjxf.conduitstratus.api.data.lang.LangKeyProvider;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@Mod("datagen_mod")
public class DataGenMod {
    public DataGenMod(IEventBus modbus) {
        modbus.addListener(this::onGatherData);
    }

    private void onGatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        var modid = Constants.MOD_ID;
        var includeClient = event.includeClient();
        var output = generator.getPackOutput();
        generator.addProvider(includeClient, new LangKeyProvider(modid, output));
        generator.addProvider(includeClient, new ConduitBlockStateProvider(output, modid, event.getExistingFileHelper()));
    }
}
