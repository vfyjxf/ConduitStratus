package dev.vfyjxf.conduitstratus.api.conduit.trait;

import org.jetbrains.annotations.Contract;

public class TraitStatus {

    private boolean sleeping;
    private final int minTickRate;
    private final int maxTickRate;
    private int currentTickRate;
    private long lastTick;

    public TraitStatus(int maxTickRate, int minTickRate) {
        this.maxTickRate = maxTickRate;
        this.minTickRate = minTickRate;
        this.currentTickRate = (maxTickRate + minTickRate) / 2;
    }

    public boolean sleeping() {
        return sleeping;
    }

    public TraitStatus sleep() {
        sleeping = true;
        return this;
    }

    public TraitStatus wake() {
        sleeping = false;
        return this;
    }

    public boolean working() {
        return !sleeping;
    }

    public TraitStatus setTickRate(int rate) {
        this.currentTickRate = Math.clamp(rate, minTickRate, maxTickRate);
        return this;
    }

    public TraitStatus setLastTick(long tick) {
        this.lastTick = tick;
        return this;
    }

    @Contract(" -> this")
    public TraitStatus speedUp() {
        return speedUp(2);
    }

    @Contract("_ -> this")
    public TraitStatus speedUp(int rate) {
        this.currentTickRate = Math.max(minTickRate, currentTickRate - rate);
        return this;
    }

    @Contract(" -> this")
    public TraitStatus speedDown() {
        return speedDown(1);
    }

    @Contract("_ -> this")
    public TraitStatus speedDown(int rate) {
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
}
