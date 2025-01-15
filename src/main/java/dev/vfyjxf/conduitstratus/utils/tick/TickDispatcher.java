package dev.vfyjxf.conduitstratus.utils.tick;

import dev.vfyjxf.conduitstratus.conduit.network.ConduitNetwork;
import dev.vfyjxf.conduitstratus.conduit.network.ConduitNetworkNode;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

/**
 * The dispatcher for ticking networks and block entities.
 */
@ApiStatus.Internal
public final class TickDispatcher {

    private static final TickDispatcher INSTANCE = new TickDispatcher();

    public static TickDispatcher instance() {
        return INSTANCE;
    }

    private final TickingNetworks tickingNetworks = new TickingNetworks();
    private final InitBlockEntities initBlockEntities = new InitBlockEntities();
    private final TickTasks tickTasks = new TickTasks();
    private long currentTick = 0;

    public long currentTick() {
        return currentTick;
    }

    public void addNetwork(ConduitNetwork network) {
        tickingNetworks.addNetwork(network);
    }

    public void removeNetwork(ConduitNetwork network) {
        tickingNetworks.removeNetwork(network);
    }

    public void addInit(BlockEntity blockEntity, Runnable runnable) {
        //noinspection ConstantConditions
        if (!blockEntity.getLevel().isClientSide()) {
            initBlockEntities.addInit(blockEntity, runnable);
        }
    }

    public void addTaskToNextTick(Runnable task) {
        tickTasks.toAdd.add(task);
    }

    public void init() {
        NeoForge.EVENT_BUS.addListener(this::onServerTickPre);
        NeoForge.EVENT_BUS.addListener(this::onServerTickPost);
        NeoForge.EVENT_BUS.addListener(this::onServerLevelTickPre);
        NeoForge.EVENT_BUS.addListener(this::onServerLevelTickPost);
        NeoForge.EVENT_BUS.addListener(this::onUnloadChunk);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::onUnloadLevel);
    }

    public void stop() {
        tickingNetworks.clear();
        initBlockEntities.clear();
    }

    private void onServerTickPre(ServerTickEvent.Pre event) {


    }

    private void onServerTickPost(ServerTickEvent.Post event) {
        for (ConduitNetwork network : tickingNetworks.networks) {
            network.tick(event.getServer(), currentTick);
        }
        currentTick++;
    }

    private void onServerLevelTickPre(LevelTickEvent.Pre event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        tickingNetworks.updateNetworks();
    }

    private void onServerLevelTickPost(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        Long2ObjectMap<MutableList<Pair<BlockEntity, Runnable>>> initEntities = initBlockEntities.getInitEntities(level);
        if (initEntities == null) return;
        var toInit = initEntities.keySet().toLongArray();
        for (long toInitPos : toInit) {
            if (level.getChunkSource().isPositionTicking(toInitPos)) {
                MutableList<Pair<BlockEntity, Runnable>> toInitChunk = initEntities.remove(toInitPos);
                if (toInitChunk == null) {
                    continue;
                }
                for (var initAction : toInitChunk) {
                    if (!initAction.getKey().isRemoved()) {
                        initAction.getValue().run();
                    }
                }
            }
        }
    }

    private void onUnloadChunk(ChunkEvent.Unload event) {
        if (event.getLevel().isClientSide()) return;
        initBlockEntities.removeChunk(event.getLevel(), event.getChunk().getPos().toLong());
    }

    private void onUnloadLevel(LevelEvent.Unload event) {
        var level = event.getLevel();

        if (level.isClientSide()) {
            return; // for no there is no reason to care about this on the client...
        }

        MutableList<ConduitNetworkNode> toDestroy = Lists.mutable.empty();

        this.tickingNetworks.updateNetworks();
        for (ConduitNetwork network : this.tickingNetworks.networks) {
            for (var node : network.getNodes()) {
                if (node.getLevel() == level) {
                    toDestroy.add(node);
                }
            }
        }

        for (ConduitNetworkNode node : toDestroy) {
            node.destroy();
        }

        this.initBlockEntities.removeLevel(level);
    }


    private static class TickingNetworks {
        private final MutableList<ConduitNetwork> networks;
        private final MutableList<ConduitNetwork> toAdd;
        private final MutableList<ConduitNetwork> toRemove;

        public TickingNetworks() {
            this.networks = Lists.mutable.empty();
            this.toAdd = Lists.mutable.empty();
            this.toRemove = Lists.mutable.empty();
        }

        public void clear() {
            this.networks.clear();
            this.toAdd.clear();
            this.toRemove.clear();
        }

        public void addNetwork(ConduitNetwork network) {
            this.toAdd.add(network);
            this.toRemove.remove(network);
        }

        public void removeNetwork(ConduitNetwork network) {
            this.toRemove.add(network);
            this.toAdd.remove(network);
        }

        public void updateNetworks() {
            if (!toRemove.isEmpty()) {
                this.networks.removeAll(this.toRemove);
                this.toRemove.clear();
            }
            if (!toAdd.isEmpty()) {
                this.networks.addAll(this.toAdd);
                this.toAdd.clear();
            }
        }
    }

    private static class InitBlockEntities {

        private final Map<LevelAccessor, Long2ObjectMap<MutableList<Pair<BlockEntity, Runnable>>>> initQueue = new Object2ObjectOpenHashMap<>();

        void addInit(BlockEntity blockEntity, Runnable initOperation) {
            Level level = blockEntity.getLevel();
            int x = blockEntity.getBlockPos().getX() >> 4;
            int z = blockEntity.getBlockPos().getZ() >> 4;
            long chunkPos = ChunkPos.asLong(x, z);
            this.initQueue.computeIfAbsent(level, key -> new Long2ObjectOpenHashMap<>())
                    .computeIfAbsent(chunkPos, key -> Lists.mutable.empty())
                    .add(Pair.of(blockEntity, initOperation));
        }

        void clear() {
            this.initQueue.clear();
        }

        synchronized void removeLevel(LevelAccessor level) {
            this.initQueue.remove(level);
        }

        synchronized void removeChunk(LevelAccessor level, long chunkPos) {
            Map<Long, MutableList<Pair<BlockEntity, Runnable>>> queue = this.initQueue.get(level);
            if (queue != null) {
                queue.remove(chunkPos);
            }
        }

        Long2ObjectMap<MutableList<Pair<BlockEntity, Runnable>>> getInitEntities(LevelAccessor level) {
            return initQueue.get(level);
        }
    }

    private static class TickTasks {
        private final MutableList<Runnable> tasks = Lists.mutable.empty();
        private final MutableList<Runnable> toAdd = Lists.mutable.empty();

        public void update() {
            if (!toAdd.isEmpty()) {
                tasks.clear();
                tasks.addAll(toAdd);
                toAdd.clear();
            }
        }
    }

}
