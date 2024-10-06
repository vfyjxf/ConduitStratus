package dev.vfyjxf.conduitstratus.api.conduit.plugin;

import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkChannel;
import dev.vfyjxf.conduitstratus.api.conduit.trait.ConduitTrait;
import net.minecraft.server.level.ServerLevel;

public interface TraitPlugin<TYPE extends ConduitTrait<TYPE>, TRAIT extends ConduitTrait<TYPE>> {

    void preHandle(ServerLevel level, TRAIT trait, NetworkChannel channel);

    void postHandle(ServerLevel level, TRAIT trait, NetworkChannel channel);

    void handle(ServerLevel level, TRAIT trait, NetworkChannel channel);

}
