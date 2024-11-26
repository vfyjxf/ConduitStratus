package dev.vfyjxf.conduitstratus.utils;

import dev.vfyjxf.conduitstratus.api.conduit.ConduitColor;
import dev.vfyjxf.conduitstratus.api.conduit.network.ChannelColor;
import net.minecraft.core.Direction;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.ImmutableList;

/**
 * A collection of commonly used enumerations, cache them to reduce memory usage
 */
public final class EnumConstant {

    public static final ImmutableList<Direction> directions = Lists.immutable.of(Direction.values());

    public static final ImmutableList<ConduitColor> conduitColors = Lists.immutable.of(ConduitColor.values());

    public static final ImmutableList<ChannelColor> channelColors = Lists.immutable.of(ChannelColor.values());

    private EnumConstant() {
    }
}
