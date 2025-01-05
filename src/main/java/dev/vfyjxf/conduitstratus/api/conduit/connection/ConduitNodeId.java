package dev.vfyjxf.conduitstratus.api.conduit.connection;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public record ConduitNodeId(ResourceKey<Level> dimension, BlockPos pos) {


}
