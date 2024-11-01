package dev.vfyjxf.conduitstratus.conduit.plugin.filter;

import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkChannels;
import dev.vfyjxf.conduitstratus.api.conduit.plugin.FilterPlugin;
import dev.vfyjxf.conduitstratus.api.conduit.trait.TransferTrait;
import dev.vfyjxf.conduitstratus.conduit.traits.item.ItemRequest;
import dev.vfyjxf.conduitstratus.conduit.traits.item.ItemResponse;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ItemFilterPlugin implements FilterPlugin<TransferTrait<ItemRequest, ItemResponse>, ItemStack, Void> {


    @Override
    public boolean apply(TransferTrait<ItemRequest, ItemResponse> trait, ItemStack stack, @Nullable Void unused) {
        return false;
    }

    @Override
    public void preHandle(ServerLevel level, TransferTrait<ItemRequest, ItemResponse> trait, NetworkChannels<TransferTrait<ItemRequest, ItemResponse>> channel) {

    }

    @Override
    public void postHandle(ServerLevel level, TransferTrait<ItemRequest, ItemResponse> trait, NetworkChannels<TransferTrait<ItemRequest, ItemResponse>> channel) {

    }

    @Override
    public void handle(ServerLevel level, TransferTrait<ItemRequest, ItemResponse> trait, NetworkChannels<TransferTrait<ItemRequest, ItemResponse>> channel) {

    }
}
