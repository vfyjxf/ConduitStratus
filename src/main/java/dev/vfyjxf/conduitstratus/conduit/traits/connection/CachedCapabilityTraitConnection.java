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

import java.util.IdentityHashMap;

public class CachedCapabilityTraitConnection<CAP> implements CapabilityConnection<CAP> {

    private final ServerLevel level;
    private final BlockCapabilityCache<? extends CAP, @Nullable Direction> cache;
    private IdentityHashMap<BlockCapability<?, @Nullable Direction>, BlockCapabilityCache<?, @Nullable Direction>> extraCaches = null;

    public CachedCapabilityTraitConnection(Trait trait, BlockCapability<? extends CAP, @Nullable Direction> token) {
        this.level = trait.getLevel();
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
    @SuppressWarnings("unchecked")
    public BlockCapability<? extends CAP, @Nullable Direction> getToken() {
        return (BlockCapability<? extends CAP, Direction>) cache.getCapability();
    }

    @Override
    public CAP getCapability() {
        //Must be not null,the connection create when capability is present
        //noinspection ConstantConditions
        return cache.getCapability();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> @Nullable T getCapability(BlockCapability<T, @Nullable Direction> capability) {
        //region lazy init
        if (extraCaches == null) {
            extraCaches = new IdentityHashMap<>(3);// item + fluid + energy + gas -1(we have save one in cache field)
        }
        //endregion
        if (capability == cache.getCapability()) return (T) cache.getCapability();//fast path
        extraCaches.computeIfAbsent(capability, key -> BlockCapabilityCache.create(
                key,
                level,
                cache.pos(),
                cache.context()
        ));
        BlockCapabilityCache<?, @Nullable Direction> capabilityCache = extraCaches.get(capability);
        if (capabilityCache != null) return (T) capabilityCache.getCapability();
        else return null;
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
