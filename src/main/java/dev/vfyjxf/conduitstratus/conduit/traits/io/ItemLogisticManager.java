package dev.vfyjxf.conduitstratus.conduit.traits.io;

import dev.vfyjxf.cloudlib.utils.ItemHandlers;
import dev.vfyjxf.conduitstratus.api.conduit.HandleType;
import dev.vfyjxf.conduitstratus.api.conduit.HandleTypes;
import dev.vfyjxf.conduitstratus.api.conduit.TickStatus;
import dev.vfyjxf.conduitstratus.api.conduit.io.LogisticManager;
import dev.vfyjxf.conduitstratus.api.conduit.network.Network;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkChannels;
import dev.vfyjxf.conduitstratus.conduit.traits.item.ItemRequest;
import dev.vfyjxf.conduitstratus.conduit.traits.item.ItemResponse;
import dev.vfyjxf.conduitstratus.conduit.traits.item.ItemTransferTrait;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.Map;

public class ItemLogisticManager implements LogisticManager<ItemTransferTrait, ItemRequest, ItemResponse> {

    @Override
    public HandleType getHandleType() {
        return HandleTypes.ITEM;
    }

    @Override
    public NetworkChannels<ItemTransferTrait> createChannels(Network network) {
        return network.createChannels(getHandleType(), ItemTransferTrait.class::isInstance);
    }

    @Override
    public void tick(Network network, NetworkChannels<ItemTransferTrait> channels, long currentTick) {
        for (var ioMap : channels.mapped()) {
            ioMap.forEach((exporter, importers) -> {
                //region check tick status
                TickStatus exporterStatus = exporter.getStatus();
                if (!exporterStatus.shouldTick(currentTick)) return;
                IItemHandler exporterHandler = exporter.getCapability();
                if (exporterHandler == null) return;
                exporterStatus.setLastTick(currentTick);
                //endregion
                //region export
                int amountToExport = exporter.exportSpeed();
                for (int slot = 0; slot < exporterHandler.getSlots() && amountToExport > 0; slot++) {
                    ItemStack toExport = exporterHandler.extractItem(slot, amountToExport, true);
                    if (toExport.isEmpty()) continue;//fast fail
                    ItemStack left = toExport;
                    for (var iterator = importers.iterator(); !left.isEmpty() && iterator.hasNext(); ) {
                        var importer = iterator.next();
                        IItemHandler importHandler = importer.getCapability();
                        if (importHandler == null) continue;
                        left = ItemHandlers.insert(importHandler, left, false);
                    }
                    int exported = toExport.getCount() - left.getCount();
                    exporterHandler.extractItem(slot, exported, false);
                    amountToExport -= exported;
                }
                //endregion
            });
        }
    }

    @Override
    public ItemResponse handleRequest(NetworkChannels<ItemTransferTrait> channels, ItemTransferTrait sender, ItemRequest request) {
        return ItemResponse.illegal("Not implemented");
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <K, V> ObjectIterator<Object2ObjectMap.Entry<K, V>> fastIterator(Map<K, V> map) {
        var entrySet = map.entrySet();
        if (entrySet instanceof Object2ObjectMap.FastEntrySet) {
            return ((Object2ObjectMap.FastEntrySet) entrySet).fastIterator();
        }
        return null;
    }

}
