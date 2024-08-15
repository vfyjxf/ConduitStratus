package dev.vfyjxf.conduitstratus.api.conduit;

import dev.vfyjxf.conduitstratus.api.conduit.trait.ConduitTrait;

public interface Conduit {

    ConduitType type();

    ConduitColor getColor();

    boolean acceptsTrait(ConduitTrait<?> trait);

    default boolean connectable(Conduit another) {
        return type() == another.type() && getColor() == another.getColor();
    }

}
