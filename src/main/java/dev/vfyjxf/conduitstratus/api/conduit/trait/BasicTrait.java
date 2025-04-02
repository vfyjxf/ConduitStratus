package dev.vfyjxf.conduitstratus.api.conduit.trait;

import dev.vfyjxf.cloudlib.api.event.EventChannel;
import dev.vfyjxf.conduitstratus.api.conduit.TickStatus;
import dev.vfyjxf.conduitstratus.api.conduit.TraitIO;
import dev.vfyjxf.conduitstratus.api.conduit.TraitType;
import dev.vfyjxf.conduitstratus.api.conduit.device.BasicAttachableDevice;
import dev.vfyjxf.conduitstratus.api.conduit.event.TraitEvent;
import dev.vfyjxf.conduitstratus.api.conduit.network.ChannelColor;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import dev.vfyjxf.conduitstratus.utils.Checks;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BasicTrait<C extends TraitConnection> extends BasicAttachableDevice implements Trait {

    protected final TraitType traitType;
    protected final EventChannel<TraitEvent> events = EventChannel.create(this);

    @NotNull
    protected TraitIO io = TraitIO.NONE;
    protected ChannelColor channelColor = ChannelColor.RED;
    @Nullable
    protected C connection;
    protected int priority = 0;

    protected BasicTrait(TraitType type, NetworkNode holder, Direction direction) {
        super(type, holder, direction);
        this.traitType = type;
    }

    @Override
    public TraitType getType() {
        return traitType;
    }

    @Override
    public BasicTrait<C> setStatus(TickStatus status) {
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
    public BasicTrait<C> setIO(TraitIO traitIO) {
        this.io = traitIO;
        return this;
    }

    @Override
    public EventChannel<TraitEvent> events() {
        return events;
    }

    @Override
    public @Nullable C getConnection() {
        return connection;
    }

    public void setConnection(@Nullable C connection) {
        this.connection = connection;
    }


    @Override
    @MustBeInvokedByOverriders
    public void saveData(CompoundTag tag, HolderLookup.Provider registries) {
    }

    @Override
    @MustBeInvokedByOverriders
    public void loadData(CompoundTag tag, HolderLookup.Provider registries) {

    }

}
