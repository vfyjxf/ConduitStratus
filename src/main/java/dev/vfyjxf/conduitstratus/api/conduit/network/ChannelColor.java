package dev.vfyjxf.conduitstratus.api.conduit.network;

//32 color
public enum ChannelColor {
    RED(0xFF0000),
    ORANGE(0xFFA500),
    YELLOW(0xFFFF00),
    LIME(0x00FF00),
    GREEN(0x008000),
    TEAL(0x008080),
    AQUA(0x00FFFF),
    CYAN(0x00FFFF),
    BLUE(0x0000FF),
    PURPLE(0x800080),
    MAGENTA(0xFF00FF),
    PINK(0xFFC0CB),
    WHITE(0xFFFFFF),
    LIGHT_GRAY(0xD3D3D3),
    GRAY(0x808080),
    BLACK(0x000000),
    BROWN(0xA52A2A),
    MAROON(0x800000),
    OLIVE(0x808000),
    LIME_GREEN(0x32CD32),
    FOREST_GREEN(0x228B22),
    SEA_GREEN(0x2E8B57),
    SKY_BLUE(0x87CEEB),
    ROYAL_BLUE(0x4169E1),
    NAVY(0x000080),
    VIOLET(0xEE82EE),
    LAVENDER(0xE6E6FA),
    FUCHSIA(0xFF00FF),
    ROSE(0xFF007F),
    CORAL(0xFF7F50),
    ORANGE_RED(0xFF4500),
    GOLD(0xFFD700);
    private final int color;

    ChannelColor(int color) {
        this.color = color;
    }
}
