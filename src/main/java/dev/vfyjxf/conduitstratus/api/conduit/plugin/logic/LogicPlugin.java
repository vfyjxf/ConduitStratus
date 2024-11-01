package dev.vfyjxf.conduitstratus.api.conduit.plugin.logic;

import dev.vfyjxf.conduitstratus.api.conduit.plugin.TraitPlugin;
import dev.vfyjxf.conduitstratus.api.conduit.trait.ConduitTrait;

public interface LogicPlugin<TRAIT extends ConduitTrait, CONTEXT>
        extends TraitPlugin<TRAIT> {

    boolean apply(TRAIT trait, CONTEXT context);

    default int tickRate() {
        return 20;
    }

}
