package dev.vfyjxf.conduitstratus.api.conduit.trait;

import dev.vfyjxf.conduitstratus.api.conduit.ConduitIO;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;

public abstract class BasicTrait<T extends ConduitTrait<T>> implements ConduitTrait<T> {

    protected final NetworkNode holder;
    protected final Direction direction;
    @NotNull
    protected ConduitIO io = ConduitIO.NONE;

    protected BasicTrait(NetworkNode holder, Direction direction) {
        this.holder = holder;
        this.direction = direction;
    }

    @Override
    public NetworkNode getHolder() {
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
    public ConduitTrait<T> setIO(ConduitIO conduitIO) {
        this.io = conduitIO;
        return this;
    }
}
