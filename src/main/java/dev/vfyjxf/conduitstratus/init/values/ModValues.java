package dev.vfyjxf.conduitstratus.init.values;

import dev.vfyjxf.conduitstratus.Constants;
import dev.vfyjxf.conduitstratus.api.conduit.Conduit;
import dev.vfyjxf.conduitstratus.api.conduit.trait.TraitType;
import dev.vfyjxf.conduitstratus.conduit.ConduitBlockItem;
import dev.vfyjxf.conduitstratus.conduit.ConduitItem;
import dev.vfyjxf.conduitstratus.conduit.block.ConduitBlock;
import dev.vfyjxf.conduitstratus.conduit.blockentity.ConduitBlockEntity;
import dev.vfyjxf.conduitstratus.conduit.conduits.BasicConduit;
import dev.vfyjxf.conduitstratus.conduit.traits.TraitItem;
import dev.vfyjxf.conduitstratus.data.ItemKeys;
import dev.vfyjxf.conduitstratus.init.TraitTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Supplier;

@EventBusSubscriber(modid = Constants.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class ModValues {

    public static void register(IEventBus modbus) {
        ITEMS.register(modbus);
        BLOCKS.register(modbus);
        CreativeTabValues.CREATIVE_TAB.register(modbus);
        BLOCK_ENTITIES.register(modbus);
    }

    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Constants.MOD_ID);
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Constants.MOD_ID);
    private static final MutableList<ConduitValue<?>> CONDUITS = Lists.mutable.empty();
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Constants.MOD_ID);

    //////////////////////////
    //        Items          /
    //////////////////////////

    //TODO:make it be a registry

    public static final ItemValue<TraitItem> itemTrait = traitItem(TraitTypes.ITEM);

    //////////////////////////
    //        Blocks         /
    //////////////////////////

    public static final BlockValue<ConduitBlock> conduitBlock = block(
            "conduit_block",
            ConduitBlock::new,
            (block, properties) -> new ConduitBlockItem(block, properties, BasicConduit.INSTANCE)
    );

    //////////////////////////
    //        Conduits       /
    //////////////////////////

    public static final ConduitValue<BasicConduit> basicConduit = conduit(
            "basic_conduit",
            () -> new ConduitItem<>(BasicConduit.INSTANCE, new Item.Properties())
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

    @SubscribeEvent
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void onCapabilityRegister(RegisterCapabilitiesEvent event) {
        for (BlockCapability<?, ?> blockCapability : BlockCapability.getAll()) {
            event.registerBlockEntity(
                    (BlockCapability) blockCapability,
                    conduitBlockEntity.get(),
                    ConduitBlockEntity.getCapabilityProvider(blockCapability)
            );
        }
    }


    private static <T extends Item> ItemValue<T> item(String name, Supplier<T> item) {
        DeferredItem<T> deferredItem = ITEMS.register(name, item);
        return new ItemValue<>(deferredItem);
    }

    private static <T extends Block> BlockValue<T> block(String name, Supplier<T> block) {
        return block(name, block, null);
    }

    private static <T extends Block> BlockValue<T> block(String name, Supplier<T> block, @Nullable BiFunction<T, Item.Properties, @NotNull BlockItem> blockItemFactory) {
        DeferredBlock<T> deferredBlock = BLOCKS.register(name, block);
        DeferredItem<BlockItem> deferredItem = ITEMS.register(name, () ->
        {
            if (blockItemFactory == null) {
                return new BlockItem(deferredBlock.get(), new Item.Properties());
            } else {
                return blockItemFactory.apply(deferredBlock.get(), new Item.Properties());
            }
        });
        CreativeTabValues.creativeTagItems.add(deferredItem);
        return new BlockValue<>(deferredBlock, deferredItem);
    }

    private static <T extends Conduit> ConduitValue<T> conduit(String name, Supplier<ConduitItem<T>> item) {
        ItemValue<ConduitItem<T>> deferredItem = item(name, item);
        ConduitValue<T> conduitValue = new ConduitValue<>(deferredItem);
        CONDUITS.add(conduitValue);
        return conduitValue;
    }

    private static ItemValue<TraitItem> traitItem(TraitType type) {
        ItemValue<TraitItem> itemValue = item(type.id().getPath() + "_trait", () -> new TraitItem(type, new Item.Properties()));
        TraitItemValues.registerTraitItem(type, itemValue);
        CreativeTabValues.creativeTagItems.add(itemValue.getItem());
        return itemValue;
    }

    public static TraitItem getTraitItem(TraitType type) {
        return (TraitItem) TraitItemValues.getTraitItem(type);
    }

    private static class TraitItemValues {
        public static final MutableMap<TraitType, ItemValue<TraitItem>> TRAIT_ITEMS = Maps.mutable.empty();

        public static void registerTraitItem(TraitType type, ItemValue<TraitItem> itemValue) {
            if (TRAIT_ITEMS.containsKey(type)) {
                throw new IllegalStateException("Trait item already registered: " + type);
            }
            TRAIT_ITEMS.put(type, itemValue);
        }

        public static Item getTraitItem(TraitType type) {
            ItemValue<TraitItem> itemValue = TRAIT_ITEMS.get(type);
            if (itemValue == null) {
                throw new IllegalStateException("Trait item not registered: " + type);
            }
            return itemValue.asItem();
        }
    }

    private static class CreativeTabValues {
        public static final DeferredRegister<CreativeModeTab> CREATIVE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Constants.MOD_ID);
        public static final MutableList<DeferredItem<?>> creativeTagItems = Lists.mutable.empty();
        public static final DeferredHolder<CreativeModeTab, CreativeModeTab> creativeTab = CREATIVE_TAB.register(
                "conduit_tab",
                () -> CreativeModeTab.builder()
                        .title(ItemKeys.tabName.get())
                        .icon(() -> conduitBlock.getItem().get().getDefaultInstance())
                        .displayItems((parameters, output) -> {
                            for (DeferredItem<?> creativeTagItem : creativeTagItems) {
                                output.accept(creativeTagItem.get());
                            }
                        })
                        .build()
        );
    }

    private ModValues() {
    }
}
