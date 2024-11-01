package dev.vfyjxf.conduitstratus.api.conduit.plugin;

import dev.vfyjxf.conduitstratus.api.conduit.trait.TransferTrait;
import org.jetbrains.annotations.Nullable;

public interface FilterPlugin<TRAIT extends TransferTrait<?, ?>, RESOURCE, CONTEXT>
        extends TraitPlugin<TRAIT> {

    default boolean apply(TRAIT trait, RESOURCE resource) {
        return apply(trait, resource, null);
    }

    boolean apply(TRAIT trait, RESOURCE resource, @Nullable CONTEXT context);
}
