package dev.vfyjxf.conduitstratus.conduit.values;

import dev.vfyjxf.conduitstratus.Constants;
import dev.vfyjxf.conduitstratus.conduit.ConduitBlockItem;
import dev.vfyjxf.conduitstratus.conduit.block.ConduitBlock;
import dev.vfyjxf.conduitstratus.conduit.blockentity.ConduitBlockEntity;
import dev.vfyjxf.conduitstratus.conduit.conduits.BasicConduit;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public final class ModValues {

    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Constants.MOD_ID);
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Constants.MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Constants.MOD_ID);

    //////////////////////////
    //        Items          /
    //////////////////////////


    //////////////////////////
    //        Blocks         /
    //////////////////////////

    public static final BlockValue<ConduitBlock> conduitBlock = block(
            "conduit_block",
            ConduitBlock::new,
            (block, properties) -> new ConduitBlockItem(block, properties, BasicConduit.INSTANCE)
    );

    //////////////////////////
    //     Block Entity      /
    //////////////////////////

    @SuppressWarnings("ConstantConditions")
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ConduitBlockEntity>> conduitBlockEntity = BLOCK_ENTITIES.register(
            "conduit_block_entity",
            () -> BlockEntityType.Builder
                    .of(ConduitBlockEntity::new, conduitBlock.getBlock().get())
                    .build(null)
    );

    private static <T extends Item> ItemValue<T> item(String name, Supplier<T> item) {
        DeferredItem<T> deferredItem = ITEMS.register(name, item);
        return new ItemValue<>(deferredItem);
    }

    private static <T extends Block> BlockValue<T> block(String name, Supplier<T> block) {
        return block(name, block, null);
    }

    private static <T extends Block> BlockValue<T> block(String name, Supplier<T> block, @Nullable BiFunction<T, Item.Properties, BlockItem> blockItemFactory) {
        DeferredBlock<T> deferredBlock = BLOCKS.register(name, block);
        DeferredItem<BlockItem> deferredItem = ITEMS.register(name, () ->
        {
            if (blockItemFactory == null) {
                return new BlockItem(deferredBlock.get(), new Item.Properties());
            } else {
                return blockItemFactory.apply(deferredBlock.get(), new Item.Properties());
            }
        });
        return new BlockValue<>(deferredBlock, deferredItem);
    }

    public static void register(IEventBus modbus) {
        ITEMS.register(modbus);
        BLOCKS.register(modbus);
        BLOCK_ENTITIES.register(modbus);
    }


    private ModValues() {
    }
}
