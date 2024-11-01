package dev.vfyjxf.conduitstratus.conduit.traits.io;

import dev.vfyjxf.conduitstratus.api.conduit.HandleType;
import dev.vfyjxf.conduitstratus.api.conduit.HandleTypes;
import dev.vfyjxf.conduitstratus.api.conduit.io.LogisticManager;
import dev.vfyjxf.conduitstratus.api.conduit.network.Network;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkChannels;
import dev.vfyjxf.conduitstratus.api.conduit.trait.TraitStatus;
import dev.vfyjxf.conduitstratus.conduit.traits.item.ItemRequest;
import dev.vfyjxf.conduitstratus.conduit.traits.item.ItemResponse;
import dev.vfyjxf.conduitstratus.conduit.traits.item.ItemTransferTrait;

public class ItemLogisticManager implements LogisticManager<ItemTransferTrait, ItemRequest, ItemResponse> {

    @Override
    public HandleType getHandleType() {
        return HandleTypes.ITEM;
    }

    @Override
    public void tickTraits(Network network, NetworkChannels<ItemTransferTrait> channels, long currentTick) {
        for (var ioMap : channels.mapped()) {
            ioMap.forEachKeyValue((exporter, importers) -> {
                TraitStatus exporterStatus = exporter.getStatus();
                if (exporterStatus.working() && exporterStatus.shouldTick(currentTick)) {
//                    var exporterExtension = exporter.extension();
                    for (var importer : importers) {

                    }
                }
            });
        }

    }

    @Override
    public ItemResponse handleRequest(NetworkChannels<ItemTransferTrait> channels, ItemTransferTrait sender, ItemRequest request) {
        return null;
    }

}
