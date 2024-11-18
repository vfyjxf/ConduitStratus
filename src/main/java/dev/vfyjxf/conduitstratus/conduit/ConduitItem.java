package dev.vfyjxf.conduitstratus.conduit;

import dev.vfyjxf.conduitstratus.api.conduit.Conduit;
import dev.vfyjxf.conduitstratus.api.conduit.ConduitColor;
import net.minecraft.world.item.Item;

public class ConduitItem<T extends Conduit> extends Item {

    private final T conduit;

    public ConduitItem(T conduit, Properties properties) {
        super(properties);
        this.conduit = conduit;
    }

    public T getConduit() {
        return conduit;
    }

    public ConduitColor getColor() {
        return conduit.color();
    }
}
