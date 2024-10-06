package dev.vfyjxf.conduitstratus.conduit.traits;

import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import dev.vfyjxf.conduitstratus.api.conduit.trait.BasicCapabilityTrait;
import dev.vfyjxf.conduitstratus.api.conduit.trait.ConduitTraitType;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

public class ItemTrait extends BasicCapabilityTrait<ItemTrait, IItemHandler> {

    protected ItemTrait(
            ConduitTraitType<ItemTrait> type,
            NetworkNode holder,
            Direction direction
    ) {
        super(type, holder, direction, Capabilities.ItemHandler.BLOCK);
    }

}
