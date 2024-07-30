package dev.vfyjxf.conduitstratus.api.conduit;

import dev.vfyjxf.conduitstratus.api.conduit.trait.IConduitTrait;

public interface IConduit {

    IConduitDefinition definition();

    ConduitColor getColor();

    boolean acceptsTrait(IConduitTrait<?> trait);

    boolean connectable(IConduit another);

}
