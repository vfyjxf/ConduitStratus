package dev.vfyjxf.conduitstratus.conduit.block;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import dev.vfyjxf.conduitstratus.conduit.ConduitConnections;
import dev.vfyjxf.conduitstratus.utils.ShapeHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.EnumMap;

import static net.minecraft.world.level.block.Block.box;

public final class ConduitShapes {

    private static final VoxelShape CENTER = box(5.0, 5.0, 5.0, 11.0, 11.0, 11.0);
    private static final VoxelShape STRAIGHT = box(5, 5, 0, 11, 11, 16);

    private static final EnumMap<Direction, VoxelShape> CONNECTIONS = new EnumMap<>(Direction.class) {
        {
            put(Direction.NORTH, combine(
                    box(5.5, 5.5, 3, 10.5, 10.5, 6),
                    box(5, 5, 0, 11, 11, 4)
            ));
            put(Direction.EAST, combine(
                    box(10, 5.5, 5.5, 13, 10.5, 10.5),
                    box(12, 5, 5, 16, 11, 11)
            ));
            put(Direction.SOUTH, combine(
                    box(5.5, 5.5, 10.5, 10.5, 10.5, 13),
                    box(5, 5, 12, 11, 11, 16)
            ));
            put(Direction.WEST, combine(
                    box(3, 5.5, 5.5, 6, 10.5, 10.5),
                    box(0, 5, 5, 4, 11, 11)
            ));
            put(Direction.UP, combine(
                    box(5.5, 10, 5.5, 10.5, 13, 10.5),
                    box(5, 12, 5, 11, 16, 11)
            ));
            put(Direction.DOWN, combine(
                    box(5.5, 3, 5.5, 10.5, 6, 10.5),
                    box(5, 0, 5, 11, 4, 11)
            ));
        }
    };

    private static final EnumMap<Direction, VoxelShape> TRAIT_SIDES = new EnumMap<>(Direction.class) {
        {
            put(Direction.NORTH, combine(
                    box(5.5, 5.5, 3, 10.5, 10.5, 6),
                    box(5, 5, 2, 11, 11, 4),
                    box(4, 4, 1.5, 12, 12, 2.5),
                    box(1, 1, 0, 15, 15, 2)
            ));
            put(Direction.EAST, combine(
                    box(10, 5.5, 5.5, 13, 10.5, 10.5),
                    box(12, 5, 5, 14, 11, 11),
                    box(13.5, 4, 4, 14.5, 12, 12),
                    box(14, 1, 1, 16, 15, 15)
            ));
            put(Direction.SOUTH, combine(
                    box(5.5, 5.5, 10.5, 10.5, 10.5, 13),
                    box(5, 5, 12, 11, 11, 14),
                    box(4, 4, 13.5, 12, 12, 14.5),
                    box(1, 1, 14, 15, 15, 16)
            ));
            put(Direction.WEST, combine(
                    box(3, 5.5, 5.5, 6, 10.5, 10.5),
                    box(0, 5, 5, 2, 11, 11),
                    box(1.5, 4, 4, 2.5, 12, 12),
                    box(0, 1, 1, 2, 15, 15)
            ));
            put(Direction.UP, combine(
                    box(5.5, 10, 5.5, 10.5, 13, 10.5),
                    box(5, 12, 5, 11, 14, 11),
                    box(4, 13.5, 4, 12, 14.5, 12),
                    box(1, 14, 1, 15, 16, 15)
            ));
            put(Direction.DOWN, combine(
                    box(5.5, 3, 5.5, 10.5, 6, 10.5),
                    box(5, 0, 5, 11, 2, 11),
                    box(4, 1.5, 4, 12, 2.5, 12),
                    box(1, 0, 1, 15, 2, 15)
            ));
        }
    };

    private static final LoadingCache<ConduitConnections, VoxelShape> CACHE = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .build(new CacheLoader<>() {
                @Override
                public VoxelShape load(ConduitConnections key) {
                    if (key.isStraight() && key.traitSides().isEmpty()) {
                        Direction direction = key.getStraightDirection();
                        return ShapeHelper.rotate(STRAIGHT, direction);
                    } else {
                        VoxelShape shape = CENTER;
                        for (Direction connection : key.connectionSides()) {
                            shape = Shapes.joinUnoptimized(shape, CONNECTIONS.get(connection), BooleanOp.OR);
                        }
                        for (Direction traitSide : key.traitSides()) {
                            shape = Shapes.joinUnoptimized(shape, TRAIT_SIDES.get(traitSide), BooleanOp.OR);
                        }
                        return shape.optimize();
                    }
                }
            });

    public static VoxelShape getShape(ConduitConnections connections) {
        return CACHE.getUnchecked(connections);
    }

    private static VoxelShape combine(VoxelShape shape, VoxelShape... shapes) {
        for (VoxelShape s : shapes) {
            shape = Shapes.joinUnoptimized(shape, s, BooleanOp.OR);
        }
        return shape.optimize();
    }

    private ConduitShapes() {
    }
}
