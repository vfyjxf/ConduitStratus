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
    private final BlockCapability<? extends CAP, ? extends @Nullable Object> token;
    private final BlockPos pos;
    private final Direction direction;

    public CapabilityTraitConnection(Trait trait, BlockCapability<? extends CAP, @Nullable Object> token) {
        this.level = trait.getLevel();
        this.token = token;
        Direction traitDirection = trait.getDirection();
        BlockPos nodePos = trait.getNode().getPos();
        this.pos = nodePos.relative(traitDirection);
        this.direction = traitDirection;
    }

    @Override
    @SuppressWarnings("unchecked")
    public BlockCapability<? extends CAP, ? extends @Nullable Direction> getToken() {
        return (BlockCapability<? extends CAP, ? extends Direction>) token;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CAP getCapability() {
        if (token.contextClass() == Void.class) {
            return level.getCapability(token, pos, null);
        } else {
            return level.getCapability((BlockCapability<? extends CAP, ? super Direction>) token, pos, direction);
        }
    }


    @Override
    public <T, C> @Nullable T getCapability(BlockCapability<T, @Nullable C> capability, @Nullable C context) {
        return level.getCapability(capability, pos, context);
    }

    @Override
    public <T> @Nullable T getCapability(BlockCapability<T, @Nullable Direction> capability) {
        return level.getCapability(capability, pos, direction.getOpposite());
    }

    @Override
    public <T> @Nullable T getCapabilityVoid(BlockCapability<T, @Nullable Void> capability) {
        return level.getCapability(capability, pos, null);
    }

    @Override
    public @Nullable BlockEntity getFacing() {
        return level.getBlockEntity(pos);
    }

    @Override
    public Direction getDirection() {
        return direction;
    }

    @Override
    public void destroy() {
        //TODO: Destroy the connection
    }
}
