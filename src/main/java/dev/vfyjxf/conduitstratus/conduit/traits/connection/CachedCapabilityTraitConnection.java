package dev.vfyjxf.conduitstratus.conduit.traits.connection;

import dev.vfyjxf.conduitstratus.api.conduit.trait.CapabilityConnection;
import dev.vfyjxf.conduitstratus.api.conduit.trait.Trait;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import org.jetbrains.annotations.Nullable;

public class CachedCapabilityTraitConnection<CAP> implements CapabilityConnection<CAP> {

    private final ServerLevel level;
    private final Direction direction;
    private final BlockCapabilityCache<? extends CAP, ?> cache;

    public CachedCapabilityTraitConnection(Trait trait, BlockCapability<? extends CAP, @Nullable Void> token) {
        this(trait, token, null);
    }

    public <C> CachedCapabilityTraitConnection(Trait trait, BlockCapability<? extends CAP, @Nullable C> token, @Nullable C context) {
        this.level = trait.getLevel();
        Direction traitDirection = trait.getDirection();
        BlockPos nodePos = trait.getNode().getPos();
        BlockPos targetPos = nodePos.relative(traitDirection);
        this.direction = traitDirection;
        cache = BlockCapabilityCache.create(
                token,
                level,
                targetPos,
                context
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public BlockCapability<? extends CAP, ? extends @Nullable Object> getToken() {
        return (BlockCapability<? extends CAP, Object>) cache.getCapability();
    }

    @Override
    public CAP getCapability() {
        return cache.getCapability();
    }

    @Override
    public <T, C> T getCapability(BlockCapability<T, @Nullable C> capability, @Nullable C context) {
        return level.getCapability(capability, cache.pos(), context);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <T> @Nullable T getCapability(BlockCapability<T, @Nullable Direction> capability) {
        if (capability == cache.getCapability())
            return (T) cache.getCapability();
        else return null;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public <T> @Nullable T getCapabilityVoid(BlockCapability<T, @Nullable Void> capability) {
        if (capability == cache.getCapability())
            return (T) cache.getCapability();
        else return null;
    }

    @Override
    public @Nullable BlockEntity getFacing() {
        return null;
    }

    @Override
    public Direction getDirection() {
        return direction;
    }

    @Override
    public void destroy() {

    }
}
