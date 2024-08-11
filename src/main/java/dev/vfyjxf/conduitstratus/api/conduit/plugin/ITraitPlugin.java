package dev.vfyjxf.conduitstratus.api.conduit.plugin;

import dev.vfyjxf.conduitstratus.api.conduit.trait.IConduitTrait;
import net.minecraft.server.level.ServerLevel;

public interface ITraitPlugin<TYPE, TRAIT extends IConduitTrait<TYPE>> {

    void preHandle(ServerLevel level, TRAIT trait);

    void postHandle(ServerLevel level, TRAIT trait);

}
