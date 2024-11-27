package dev.vfyjxf.conduitstratus.api.conduit.trait;

import dev.vfyjxf.cloudlib.api.event.EventChannel;
import dev.vfyjxf.conduitstratus.api.conduit.TraitIO;
import dev.vfyjxf.conduitstratus.api.conduit.data.DataKey;
import dev.vfyjxf.conduitstratus.api.conduit.event.TraitEvent;
import dev.vfyjxf.conduitstratus.api.conduit.network.ChannelColor;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import dev.vfyjxf.conduitstratus.utils.Checks;
import net.minecraft.core.Direction;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.map.MutableMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BasicTrait<C extends TraitConnection> implements Trait {

    protected final TraitType type;
    protected final NetworkNode holder;
    protected final Direction direction;
    protected final EventChannel<TraitEvent> eventChannel = EventChannel.create(this);
    protected final MutableMap<DataKey<?>, @Nullable Object> dataMap = Maps.mutable.withInitialCapacity(2);
    @NotNull
    protected TraitIO io = TraitIO.NONE;
    protected ChannelColor channelColor = ChannelColor.RED;
    protected TraitStatus status = null;
    @Nullable
    protected C connection;
    protected int priority = 0;
    protected final String identifier = "";

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
    public String identifier() {
        return identifier;
    }

    @Override
    public Direction getDirection() {
        return direction;
    }

    @Override
    public TraitIO getIO() {
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
    public Trait setIO(TraitIO traitIO) {
        this.io = traitIO;
        return this;
    }

    @Override
    public <T> void attach(DataKey<T> key, @Nullable T value) {
        dataMap.put(key, value);
    }

    @Override
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public <T> T get(DataKey<T> key) {
        return (T) dataMap.get(key);
    }

    @Override
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public <T> T detach(DataKey<T> key) {
        return (T) dataMap.remove(key);
    }

    @Override
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public <T> T getOrDefault(DataKey<T> key, @Nullable T defaultValue) {
        return (T) dataMap.getOrDefault(key, defaultValue);
    }

    @Override
    public void clear() {
        dataMap.clear();
    }

    @Override
    public boolean isEmpty() {
        return dataMap.isEmpty();
    }

    @Override
    public boolean has(DataKey<?> key) {
        return dataMap.containsKey(key);
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
