package dev.vfyjxf.conduitstratus.conduit.traits.item;

import dev.vfyjxf.conduitstratus.api.conduit.TickStatus;
import dev.vfyjxf.conduitstratus.api.conduit.TraitIO;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import dev.vfyjxf.conduitstratus.api.conduit.trait.BasicTransferCapabilityTrait;
import dev.vfyjxf.conduitstratus.api.conduit.trait.TraitType;
import dev.vfyjxf.conduitstratus.conduit.traits.connection.CachedCapabilityTraitConnection;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

public abstract class ItemTransferTrait extends BasicTransferCapabilityTrait<IItemHandler, ItemRequest, ItemResponse> {

    /**
     * The speed at which items are exported from this trait.
     * <p>
     * Unit: items per tick
     */
    protected int exportSpeed = 16;

    protected ItemTransferTrait(
            TraitType type,
            NetworkNode holder,
            Direction direction
    ) {
        super(type, holder, direction, Capabilities.ItemHandler.BLOCK);
        this.connection = new CachedCapabilityTraitConnection<>(
                this,
                Capabilities.ItemHandler.BLOCK,
                direction.getOpposite()
        );
    }

    public int exportSpeed() {
        return exportSpeed;
    }

    public void setExportSpeed(int exportSpeed) {
        this.exportSpeed = exportSpeed;
    }

    @Override
    public ItemTransferTrait setStatus(TickStatus status) {
        super.setStatus(status);
        return this;
    }

    @Override
    public ItemTransferTrait setIO(TraitIO traitIO) {
        super.setIO(traitIO);
        return this;
    }

    @Override
    public ItemTransferTrait setPriority(int priority) {
        super.setPriority(priority);
        return this;
    }

}
