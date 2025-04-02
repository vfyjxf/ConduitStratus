package dev.vfyjxf.conduitstratus.data;

import dev.vfyjxf.cloudlib.data.lang.LangBuilder;
import dev.vfyjxf.cloudlib.data.lang.LangEntry;
import dev.vfyjxf.cloudlib.data.lang.LangProvider;
import dev.vfyjxf.conduitstratus.StratusConstants;

@LangProvider
public final class ItemKeys {

    private static final LangBuilder creativeTab = LangBuilder.create(StratusConstants.MOD_ID, "creative_tab");

    public static final LangEntry tabName = creativeTab.define("name", "Conduit Stratus");


    private ItemKeys() {
    }
}
