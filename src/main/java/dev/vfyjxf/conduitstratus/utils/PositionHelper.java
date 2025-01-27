package dev.vfyjxf.conduitstratus.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;

public class PositionHelper {

    public static Direction getDirection(Vec3i from, Vec3i to, int maxDistance) {
        Vec3i diff = to.subtract(from);
        int distManhattan = Math.abs(diff.getX()) + Math.abs(diff.getY()) + Math.abs(diff.getZ());
        if (distManhattan > maxDistance) {
            return null;
        }
        return Direction.fromDelta(diff.getX(), diff.getY(), diff.getZ());
    }
}
