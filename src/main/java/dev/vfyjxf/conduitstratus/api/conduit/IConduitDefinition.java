package dev.vfyjxf.conduitstratus.api.conduit;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface IConduitDefinition {

    Item getConduitItem();

    default ItemStack createStack(int count) {
        return new ItemStack(getConduitItem(), count);
    }

}
