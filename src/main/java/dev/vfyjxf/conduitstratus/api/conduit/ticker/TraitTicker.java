package dev.vfyjxf.conduitstratus.api.conduit.ticker;

import dev.vfyjxf.conduitstratus.api.conduit.network.Network;
import dev.vfyjxf.conduitstratus.api.conduit.trait.ConduitTrait;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Range;

public interface TraitTicker<T extends ConduitTrait<T>> extends NetworkTicker {

    void tickTrait(ServerLevel level, Network network, ConduitTrait<T> trait);

    @Override
    default @Range(from = 1, to = Integer.MAX_VALUE) int tickRate() {
        return 5;
    }
}
