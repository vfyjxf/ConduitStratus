package dev.vfyjxf.conduitstratus.conduit.traits.item;

import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import dev.vfyjxf.conduitstratus.api.conduit.trait.PoxyTrait;
import dev.vfyjxf.conduitstratus.conduit.capability.PoxyCapabilityContainer;
import dev.vfyjxf.conduitstratus.init.TraitTypes;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

/**
 * Item Capability Poxy: Allow direct external access to the exporter's importers.
 */
public class ItemTrait extends ItemTransferTrait implements PoxyTrait<IItemHandler> {

    private final PoxyCapabilityContainer poxyContainer = new PoxyCapabilityContainer();

    public ItemTrait(NetworkNode holder, Direction direction) {
        super(TraitTypes.ITEM, holder, direction, Capabilities.ItemHandler.BLOCK);
    }

    @Override
    public ItemRequest sendRequest() {
        return null;
    }

    @Override
    public ItemResponse handleRequest(ItemRequest request) {
        return null;
    }

    @Override
    public BlockCapability<IItemHandler, ?> getPoxyToken() {
        return Capabilities.ItemHandler.BLOCK;
    }

    @Override
    public @Nullable IItemHandler poxyCapability() {
        if (getIO().output()) {

        }
        return null;
    }

}
