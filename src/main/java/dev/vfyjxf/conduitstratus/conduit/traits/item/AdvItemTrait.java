package dev.vfyjxf.conduitstratus.conduit.traits.item;

import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import dev.vfyjxf.conduitstratus.api.conduit.trait.PoxyTrait;
import dev.vfyjxf.conduitstratus.api.conduit.trait.TraitType;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

/**
 * Item Capability Poxy: Allow direct external access to the exporter's corresponding importers.
 */
public class AdvItemTrait extends ItemTransferTrait implements PoxyTrait<IItemHandler> {

    public AdvItemTrait(TraitType type, NetworkNode holder, Direction direction, BlockCapability<? extends IItemHandler, @Nullable Direction> token) {
        super(type, holder, direction, token);
    }

    @Override
    public boolean hasRequest() {
        return false;
    }

    @Override
    public ItemRequest sendRequest() {
        return null;
    }

    @Override
    public ItemResponse handleRequest(ItemRequest request) {
        return null;
    }

    //Capability proxy

    @Override
    public BlockCapability<IItemHandler, @Nullable Direction> getPoxyToken() {
        return Capabilities.ItemHandler.BLOCK;
    }

    @Override
    public @Nullable IItemHandler poxyCapability() {
        return super.getCapability();
    }
}
