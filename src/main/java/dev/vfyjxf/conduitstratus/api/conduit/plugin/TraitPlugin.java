package dev.vfyjxf.conduitstratus.api.conduit.plugin;

import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkChannels;
import dev.vfyjxf.conduitstratus.api.conduit.trait.Trait;
import net.minecraft.server.level.ServerLevel;

//TODO:rework this, event driven would be better?
public interface TraitPlugin<TRAIT extends Trait> {

    void preHandle(ServerLevel level, TRAIT trait, NetworkChannels<TRAIT> channel);

    void postHandle(ServerLevel level, TRAIT trait, NetworkChannels<TRAIT> channel);

    void handle(ServerLevel level, TRAIT trait, NetworkChannels<TRAIT> channel);

}
