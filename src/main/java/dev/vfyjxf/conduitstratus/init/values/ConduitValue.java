package dev.vfyjxf.conduitstratus.init.values;

import dev.vfyjxf.conduitstratus.api.conduit.Conduit;
import dev.vfyjxf.conduitstratus.conduit.ConduitItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

public final class ConduitValue<T extends Conduit> implements ItemLike {
    private final ItemValue<ConduitItem<T>> item;

    public ConduitValue(ItemValue<ConduitItem<T>> item) {
        this.item = item;
    }

    @Override
    public Item asItem() {
        return item.asItem();
    }
}
