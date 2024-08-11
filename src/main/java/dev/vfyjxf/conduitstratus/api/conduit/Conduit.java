package dev.vfyjxf.conduitstratus.api.conduit;

import dev.vfyjxf.conduitstratus.api.conduit.trait.ConduitTrait;

public interface Conduit {

    ConduitType type();

    ConduitColor getColor();

    boolean acceptsTrait(ConduitTrait<?> trait);

    boolean connectable(Conduit another);

}
