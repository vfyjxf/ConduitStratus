package dev.vfyjxf.conduitstratus.api.conduit;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public record ConduitType(Item conduitItem) {

    public ItemStack createStack(int count) {
        return new ItemStack(conduitItem, count);
    }

}
