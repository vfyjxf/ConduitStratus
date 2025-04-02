package dev.vfyjxf.conduitstratus.api.conduit;

import dev.vfyjxf.conduitstratus.api.conduit.device.Device;
import dev.vfyjxf.conduitstratus.api.conduit.trait.Trait;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

/**
 * {@link TickStatus} defines the tick rate of a device({@link Trait} and {@link Device}).
 */
public class TickStatus {

    private boolean sleeping = false;
    private final int minTickPartition;
    private final int maxTickPartition;
    private int currentTickPartition;
    private long lastTick;

    public TickStatus(int maxTickPartition, int minTickPartition) {
        this.maxTickPartition = maxTickPartition;
        this.minTickPartition = minTickPartition;
        this.currentTickPartition = (maxTickPartition + minTickPartition) / 2;
    }

    public boolean sleeping() {
        return sleeping;
    }

    public TickStatus sleep() {
        sleeping = true;
        return this;
    }

    public TickStatus wakeup() {
        sleeping = false;
        return this;
    }

    public boolean working() {
        return !sleeping;
    }

    public TickStatus setTickPartition(int rate) {
        this.currentTickPartition = Math.clamp(rate, minTickPartition, maxTickPartition);
        return this;
    }

    @ApiStatus.Internal
    public TickStatus setLastTick(long tick) {
        this.lastTick = tick;
        return this;
    }

    @Contract(" -> this")
    public TickStatus speedUp() {
        return speedUp(2);
    }

    @Contract("_ -> this")
    public TickStatus speedUp(int rate) {
        this.currentTickPartition = Math.max(minTickPartition, currentTickPartition - rate);
        return this;
    }

    @Contract(" -> this")
    public TickStatus speedDown() {
        return speedDown(1);
    }

    @Contract("_ -> this")
    public TickStatus speedDown(int rate) {
        this.currentTickPartition = Math.min(maxTickPartition, currentTickPartition + rate);
        return this;
    }

    public int minTickPartition() {
        return minTickPartition;
    }

    public int maxTickPartition() {
        return maxTickPartition;
    }

    public int currentTickPartition() {
        return currentTickPartition;
    }

    public boolean shouldTick(long currentTick) {
        return currentTick >= lastTick + currentTickPartition;
    }

    public boolean alreadyTick(long currentTick) {
        return currentTick == lastTick + currentTickPartition;
    }

    public boolean shouldWork(long currentTick) {
        return !sleeping && currentTick >= lastTick + currentTickPartition;
    }
}
