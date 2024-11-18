package dev.vfyjxf.conduitstratus.init.values;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

public final class BlockValue<T extends Block> implements ItemLike {

    private final DeferredBlock<T> block;
    private final DeferredItem<BlockItem> item;

    public BlockValue(DeferredBlock<T> block, DeferredItem<BlockItem> item) {
        this.block = block;
        this.item = item;
    }

    public DeferredBlock<T> getBlock() {
        return block;
    }

    public DeferredItem<BlockItem> getItem() {
        return item;
    }

    @Override
    public Item asItem() {
        return item.asItem();
    }
}
