package dev.vfyjxf.conduitstratus.conduit.traits;

import dev.vfyjxf.conduitstratus.api.conduit.event.TraitEvent;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import dev.vfyjxf.conduitstratus.api.conduit.trait.BasicTrait;
import dev.vfyjxf.conduitstratus.api.conduit.trait.ConduitTraitType;
import dev.vfyjxf.conduitstratus.api.conduit.trait.TraitConnection;
import dev.vfyjxf.conduitstratus.api.event.IEventChannel;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

public class ItemTrait extends BasicTrait<ItemTrait> {

    protected ItemTrait(NetworkNode holder, Direction direction) {
        super(holder, direction);
    }

    @Override
    public ConduitTraitType<ItemTrait> getType() {
        return null;
    }

    @Override
    public @Nullable TraitConnection getConnection() {
        return null;
    }

    @Override
    public boolean perHandle() {
        return false;
    }

    @Override
    public boolean handle() {
        return false;
    }

    @Override
    public boolean postHandle() {
        return false;
    }

    @Override
    public IEventChannel<TraitEvent> events() {
        return null;
    }
}
