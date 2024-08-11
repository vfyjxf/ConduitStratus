package dev.vfyjxf.conduitstratus.api.conduit;

import dev.vfyjxf.conduitstratus.api.conduit.trait.IConduitTrait;

public interface IConduit {

    ConduitType type();

    ConduitColor getColor();

    boolean acceptsTrait(IConduitTrait<?> trait);

    boolean connectable(IConduit another);

}
