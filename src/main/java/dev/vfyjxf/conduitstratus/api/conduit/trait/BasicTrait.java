package dev.vfyjxf.conduitstratus.api.conduit.trait;

import dev.vfyjxf.conduitstratus.api.conduit.ConduitIO;
import dev.vfyjxf.conduitstratus.api.conduit.network.INetworkNode;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;

public abstract class BasicTrait<T extends IConduitTrait<T>> implements IConduitTrait<T> {

    protected final INetworkNode holder;
    protected final Direction direction;
    @NotNull
    protected ConduitIO io = ConduitIO.NONE;

    protected BasicTrait(INetworkNode holder, Direction direction) {
        this.holder = holder;
        this.direction = direction;
    }

    @Override
    public INetworkNode getHolder() {
        return holder;
    }

    @Override
    public Direction getDirection() {
        return direction;
    }

    @Override
    public ConduitIO getIO() {
        return io;
    }

    @Override
    public IConduitTrait<T> setIO(ConduitIO conduitIO) {
        this.io = conduitIO;
        return this;
    }
}
