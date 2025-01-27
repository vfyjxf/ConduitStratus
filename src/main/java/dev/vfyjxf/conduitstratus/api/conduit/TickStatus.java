package dev.vfyjxf.conduitstratus.api.conduit;

import dev.vfyjxf.conduitstratus.api.conduit.device.NetworkDevice;
import dev.vfyjxf.conduitstratus.api.conduit.trait.Trait;
import org.jetbrains.annotations.Contract;

/**
 * {@link TickStatus} defines the status of the tick rate of a device({@link Trait} and {@link NetworkDevice}).
 */
public class TickStatus {

    private boolean sleeping = false;
    private final int minTickRate;
    private final int maxTickRate;
    private int currentTickRate;
    private long lastTick;

    public TickStatus(int maxTickRate, int minTickRate) {
        this.maxTickRate = maxTickRate;
        this.minTickRate = minTickRate;
        this.currentTickRate = (maxTickRate + minTickRate) / 2;
    }

    public boolean sleeping() {
        return sleeping;
    }

    public TickStatus sleep() {
        sleeping = true;
        return this;
    }

    public TickStatus wake() {
        sleeping = false;
        return this;
    }

    public boolean working() {
        return !sleeping;
    }

    public TickStatus setTickRate(int rate) {
        this.currentTickRate = Math.clamp(rate, minTickRate, maxTickRate);
        return this;
    }

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
        this.currentTickRate = Math.max(minTickRate, currentTickRate - rate);
        return this;
    }

    @Contract(" -> this")
    public TickStatus speedDown() {
        return speedDown(1);
    }

    @Contract("_ -> this")
    public TickStatus speedDown(int rate) {
        this.currentTickRate = Math.min(maxTickRate, currentTickRate + rate);
        return this;
    }

    public int minTickRate() {
        return minTickRate;
    }

    public int maxTickRate() {
        return maxTickRate;
    }

    public int currentTickRate() {
        return currentTickRate;
    }

    public boolean shouldTick(long currentTick) {
        return currentTick >= lastTick + currentTickRate;
    }

    public boolean shouldWork(long currentTick) {
        return working() && shouldTick(currentTick);
    }
}
