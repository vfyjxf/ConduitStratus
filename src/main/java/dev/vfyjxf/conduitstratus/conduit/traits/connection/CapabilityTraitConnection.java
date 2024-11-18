package dev.vfyjxf.conduitstratus.conduit.traits.connection;

import dev.vfyjxf.conduitstratus.api.conduit.trait.CapabilityConnection;
import dev.vfyjxf.conduitstratus.api.conduit.trait.Trait;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

public class CapabilityTraitConnection<CAP> implements CapabilityConnection<CAP> {

    private final Level level;
    private final BlockCapability<? extends CAP, @Nullable Direction> token;
    private final BlockPos pos;
    private final Direction direction;

    public CapabilityTraitConnection(Trait trait, BlockCapability<? extends CAP, @Nullable Direction> token) {
        this.level = trait.getLevel();
        this.token = token;
        Direction traitDirection = trait.getDirection();
        BlockPos nodePos = trait.getNode().getPos();
        this.pos = nodePos.relative(traitDirection);
        this.direction = traitDirection.getOpposite();
    }

    @Override
    public BlockCapability<? extends CAP, @Nullable Direction> getToken() {
        return token;
    }

    @Override
    public CAP getCapability() {
        //Must be not null,the connection create when capability is present
        //noinspection ConstantConditions
        return level.getCapability(token, pos, direction);
    }


    @Override
    public <T> @Nullable T getCapability(BlockCapability<T, @Nullable Direction> capability) {
        return level.getCapability(capability, pos, direction);
    }

    @Override
    public @Nullable BlockEntity getFacing() {
        return level.getBlockEntity(pos);
    }

    @Override
    public Direction getDirection() {
        return direction.getOpposite();
    }

    @Override
    public void destroy() {
        //TODO: Destroy the connection
    }
}
