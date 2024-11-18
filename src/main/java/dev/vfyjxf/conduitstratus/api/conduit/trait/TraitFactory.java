package dev.vfyjxf.conduitstratus.api.conduit.trait;

import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import net.minecraft.core.Direction;

public interface TraitFactory {

    Trait create(TraitType type, NetworkNode holder, Direction direction);

}
