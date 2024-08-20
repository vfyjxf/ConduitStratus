package dev.vfyjxf.conduitstratus.api.conduit.trait;

import dev.vfyjxf.conduitstratus.api.conduit.ConduitIO;
import dev.vfyjxf.conduitstratus.api.conduit.event.TraitEvent;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import dev.vfyjxf.conduitstratus.api.event.EventChannel;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BasicTrait<T extends ConduitTrait<T>, C extends TraitConnection> implements ConduitTrait<T> {

    protected final ConduitTraitType<T> type;
    protected final NetworkNode holder;
    protected final Direction direction;
    protected final EventChannel<TraitEvent> eventChannel = EventChannel.create(this);
    @NotNull
    protected ConduitIO io = ConduitIO.NONE;
    @Nullable
    protected C connection;

    protected BasicTrait(ConduitTraitType<T> type, NetworkNode holder, Direction direction) {
        this.type = type;
        this.holder = holder;
        this.direction = direction;
    }

    @Override
    public ConduitTraitType<T> getType() {
        return type;
    }

    @Override
    public NetworkNode getNode() {
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

    @Override
    public EventChannel<TraitEvent> events() {
        return eventChannel;
    }

    @Override
    public @Nullable C getConnection() {
        return connection;
    }

    public void setConnection(@Nullable C connection) {
        this.connection = connection;
    }
}
