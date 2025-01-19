package dev.vfyjxf.conduitstratus.conduit.plugin.filter;

import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkChannels;
import dev.vfyjxf.conduitstratus.api.conduit.plugin.FilterPlugin;
import dev.vfyjxf.conduitstratus.conduit.traits.item.ItemTransferTrait;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ItemFilterPlugin implements FilterPlugin<ItemTransferTrait, ItemStack, Void> {

    @Override
    public boolean apply(
            ItemTransferTrait trait,
            ItemStack stack,
            @Nullable Void unused
    ) {
        return false;
    }

    @Override
    public void preHandle(
            ServerLevel level,
            ItemTransferTrait trait,
            NetworkChannels<ItemTransferTrait> channels
    ) {

    }

    @Override
    public void postHandle(
            ServerLevel level,
            ItemTransferTrait trait,
            NetworkChannels<ItemTransferTrait> channels
    ) {

    }

    @Override
    public void handle(
            ServerLevel level,
            ItemTransferTrait trait,
            NetworkChannels<ItemTransferTrait> channels
    ) {

    }
}
