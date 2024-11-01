package dev.vfyjxf.conduitstratus.api.conduit.trait;

import dev.vfyjxf.conduitstratus.api.conduit.ConduitIO;
import dev.vfyjxf.conduitstratus.api.conduit.event.TraitEvent;
import dev.vfyjxf.conduitstratus.api.conduit.network.ChannelColor;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import dev.vfyjxf.conduitstratus.api.event.EventChannel;
import dev.vfyjxf.conduitstratus.utils.Checks;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BasicTrait<C extends TraitConnection> implements ConduitTrait {

    protected final TraitType type;
    protected final NetworkNode holder;
    protected final Direction direction;
    protected final EventChannel<TraitEvent> eventChannel = EventChannel.create(this);
    @NotNull
    protected ConduitIO io = ConduitIO.NONE;
    protected ChannelColor channelColor = ChannelColor.RED;
    protected TraitStatus status = null;
    @Nullable
    protected C connection;
    protected int priority = 0;

    protected BasicTrait(TraitType type, NetworkNode holder, Direction direction) {
        this.type = type;
        this.holder = holder;
        this.direction = direction;
    }

    @Override
    public TraitType getType() {
        return type;
    }

    @Override
    public NetworkNode getNode() {
        return holder;
    }

    @Override
    public TraitStatus getStatus() {
        return status;
    }

    @Override
    public BasicTrait<C> setStatus(TraitStatus status) {
        this.status = status;
        return this;
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
    public ChannelColor getChannel() {
        return channelColor;
    }

    @Override
    public int priority() {
        return priority;
    }

    @Override
    public BasicTrait<C> setPriority(int priority) {
        Checks.checkArgument(priority >= 0, "Priority must be greater than or equal to 0");
        this.priority = priority;
        return this;
    }

    @Override
    public ConduitTrait setIO(ConduitIO conduitIO) {
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
