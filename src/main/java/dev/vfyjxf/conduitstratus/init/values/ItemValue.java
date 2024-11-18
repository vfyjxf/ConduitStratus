package dev.vfyjxf.conduitstratus.init.values;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredItem;

public final class ItemValue<T extends Item> implements ItemLike {

    private final DeferredItem<T> item;

    public ItemValue(DeferredItem<T> item) {
        this.item = item;
    }

    public DeferredItem<T> getItem() {
        return item;
    }

    @Override
    public Item asItem() {
        return item.asItem();
    }
}
