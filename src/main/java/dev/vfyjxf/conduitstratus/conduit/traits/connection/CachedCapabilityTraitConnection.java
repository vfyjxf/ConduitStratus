package dev.vfyjxf.conduitstratus.conduit.traits.connection;

import dev.vfyjxf.conduitstratus.api.conduit.trait.CapabilityConnection;
import dev.vfyjxf.conduitstratus.api.conduit.trait.ConduitTrait;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;

public class CachedCapabilityTraitConnection<CAP> implements CapabilityConnection<CAP> {

    private final ServerLevel level;
    private final BlockCapabilityCache<? extends CAP, @Nullable Direction> cache;
    private IdentityHashMap<BlockCapability<?, @Nullable Direction>, BlockCapabilityCache<?, @Nullable Direction>> extraCaches = null;

    public CachedCapabilityTraitConnection(ConduitTrait<?> trait, BlockCapability<? extends CAP, @Nullable Direction> token) {
        if (trait.getLevel() instanceof ServerLevel) {
            this.level = (ServerLevel) trait.getLevel();
        } else {
            throw new IllegalArgumentException("The level must be a ServerLevel");
        }
        Direction traitDirection = trait.getDirection();
        BlockPos nodePos = trait.getNode().getPos();
        BlockPos targetPos = nodePos.relative(traitDirection);
        cache = BlockCapabilityCache.create(
                token,
                level,
                targetPos,
                traitDirection.getOpposite()
        );
    }

    @Override
    public BlockCapability<? extends CAP, @Nullable Direction> getToken() {
        return null;
    }

    @Override
    public CAP getCapability() {
        //Must be not null,the connection create when capability is present
        //noinspection ConstantConditions
        return cache.getCapability();
    }

    @Override
    public <T> @Nullable T getCapability(BlockCapability<T, @Nullable Direction> capability) {
        //region lazy init
        if (extraCaches == null) {
            extraCaches = new IdentityHashMap<>(6);
        }
        //endregion
        return null;
    }

    @Override
    public @Nullable BlockEntity getFacing() {
        return null;
    }

    @Override
    public Direction getDirection() {
        //noinspection ConstantConditions
        return cache.context().getOpposite();
    }

    @Override
    public void destroy() {

    }
}
