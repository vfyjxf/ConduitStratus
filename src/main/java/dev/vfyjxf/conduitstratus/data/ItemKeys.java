package dev.vfyjxf.conduitstratus.data;

import dev.vfyjxf.conduitstratus.Constants;
import dev.vfyjxf.conduitstratus.api.data.lang.LangBuilder;
import dev.vfyjxf.conduitstratus.api.data.lang.LangEntry;
import dev.vfyjxf.conduitstratus.api.data.lang.LangProvider;

@LangProvider
public final class ItemKeys {

    private static final LangBuilder creativeTab = LangBuilder.create(Constants.MOD_ID, "creative_tab");

    public static final LangEntry tabName = creativeTab.define("name", "Conduit Stratus");


    private ItemKeys() {
    }
}
