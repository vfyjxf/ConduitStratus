package dev.vfyjxf.conduitstratus.conduit;

import dev.vfyjxf.conduitstratus.api.conduit.ConduitColor;
import net.minecraft.world.item.Item;

public class ConduitItem extends Item {

    private final ConduitColor color;

    public ConduitItem(ConduitColor color, Properties properties) {
        super(properties);
        this.color = color;
    }

    public ConduitColor getColor() {
        return color;
    }
}
