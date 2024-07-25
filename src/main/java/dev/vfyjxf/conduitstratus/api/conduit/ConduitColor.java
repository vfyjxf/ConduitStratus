package dev.vfyjxf.conduitstratus.api.conduit;

import net.minecraft.world.item.DyeColor;

public enum ConduitColor {
    WHITE(0xFFFFFF, DyeColor.WHITE),
    ORANGE(0xFFA500, DyeColor.ORANGE),
    MAGENTA(0xFF00FF, DyeColor.MAGENTA),
    LIGHT_BLUE(0xADD8E6, DyeColor.LIGHT_BLUE),
    YELLOW(0xFFFF00, DyeColor.YELLOW),
    LIME(0x00FF00, DyeColor.LIME),
    PINK(0xFFC0CB, DyeColor.PINK),
    GRAY(0x808080, DyeColor.GRAY),
    LIGHT_GRAY(0xD3D3D3, DyeColor.LIGHT_GRAY),
    CYAN(0x00FFFF, DyeColor.CYAN),
    PURPLE(0x800080, DyeColor.PURPLE),
    BLUE(0x0000FF, DyeColor.BLUE),
    BROWN(0xA52A2A, DyeColor.BROWN),
    GREEN(0x008000, DyeColor.GREEN),
    RED(0xFF0000, DyeColor.RED),
    BLACK(0x000000, DyeColor.BLACK);


    public final int color;
    public final DyeColor dyeColor;

    ConduitColor(int color, DyeColor dyeColor) {
        this.color = color;
        this.dyeColor = dyeColor;
    }
}
