package dev.vfyjxf.conduitstratus.conduit.traits.item;

import dev.vfyjxf.conduitstratus.api.conduit.ConduitIO;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import dev.vfyjxf.conduitstratus.api.conduit.trait.BasicTransferCapabilityTrait;
import dev.vfyjxf.conduitstratus.api.conduit.trait.TraitStatus;
import dev.vfyjxf.conduitstratus.api.conduit.trait.TraitType;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public abstract class ItemTransferTrait extends BasicTransferCapabilityTrait<IItemHandler, ItemRequest, ItemResponse> {

    protected ItemTransferTrait(
            TraitType type,
            NetworkNode holder,
            Direction direction,
            BlockCapability<? extends IItemHandler, @Nullable Direction> token
    ) {
        super(type, holder, direction, token);
    }

    @Override
    public ItemTransferTrait setStatus(TraitStatus status) {
        super.setStatus(status);
        return this;
    }

    @Override
    public ItemTransferTrait setIO(ConduitIO conduitIO) {
        super.setIO(conduitIO);
        return this;
    }

    @Override
    public ItemTransferTrait setPriority(int priority) {
        super.setPriority(priority);
        return this;
    }

}
