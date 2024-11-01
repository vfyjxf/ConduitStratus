package dev.vfyjxf.conduitstratus.datagen;

import dev.vfyjxf.conduitstratus.Constants;
import dev.vfyjxf.conduitstratus.api.data.lang.LangKeyProvider;
import net.minecraft.data.DataProvider;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@Mod("datagen-mod")
public class DataGenMod {
    public DataGenMod(IEventBus modbus) {
        modbus.addListener((GatherDataEvent event) -> {
            event.getGenerator().addProvider(
                    event.includeClient(),
                    (DataProvider.Factory<DataProvider>) (output) -> new LangKeyProvider(Constants.MOD_ID, output)
            );
        });
    }
}
