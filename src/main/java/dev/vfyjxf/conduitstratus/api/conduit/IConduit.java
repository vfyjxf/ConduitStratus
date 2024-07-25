package dev.vfyjxf.conduitstratus.api.conduit;

import dev.vfyjxf.conduitstratus.api.conduit.trait.IConduitTrait;
import org.eclipse.collections.api.list.MutableList;

public interface IConduit {

    IConduitDefinition definition();

    ConduitColor getColor();

    boolean acceptsTrait(IConduitTrait<?> trait);

    <TYPE> MutableList<IConduitTrait<TYPE>> getTraits(ITypeDefinition<TYPE> type);


}
