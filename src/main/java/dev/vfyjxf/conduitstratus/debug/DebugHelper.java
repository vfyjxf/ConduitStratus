package dev.vfyjxf.conduitstratus.debug;

import dev.vfyjxf.conduitstratus.api.conduit.connection.ConduitDistance;
import dev.vfyjxf.conduitstratus.api.conduit.connection.ConduitNodeId;
import dev.vfyjxf.conduitstratus.api.conduit.network.BaseNetwork;
import dev.vfyjxf.conduitstratus.api.conduit.network.Network;
import dev.vfyjxf.conduitstratus.conduit.blockentity.ConduitBlockEntity;
import dev.vfyjxf.conduitstratus.init.values.ModValues;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber
public class DebugHelper {

    @SubscribeEvent
    public static void onItemRightClick(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getEntity().isShiftKeyDown()) {
            return;
        }

        Player player = event.getEntity();
        if (player.level().isClientSide) {
            NetworkDebugRender.entries = null;
            return;
        }
        var heldItem = event.getItemStack();

        // TODO: debug item
        if (!heldItem.is(Items.STICK)) {
            return;
        }

        var block = event.getLevel().getBlockState(event.getPos());

        if (!block.is(ModValues.conduitBlock.getBlock())) {
            return;
        }

        debugNetwork(player, event.getPos());


    }


    public static void debugNetwork(Player player, BlockPos pos) {
        var level = player.level();
        var blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof ConduitBlockEntity conduitBlockEntity)) {
            return;
        }

        BaseNetwork baseNetwork = conduitBlockEntity.getNetwork();
        if (!(baseNetwork instanceof Network network)) {
            return;
        }

        ConduitDistance distance = network.getDistance();

        ConduitNodeId fromId = conduitBlockEntity.conduitId();

        List<DebugPackage.Entry> entries = new ArrayList<>();

        for (ConduitNodeId toId : network.nodeIds()) {
            var distanceTo = distance.getDistance(fromId, toId);

            entries.add(new DebugPackage.Entry(toId.pos(), toId.dimension(), distanceTo));

        }

        DebugPackage debugPackage = new DebugPackage(entries);

        PacketDistributor.sendToPlayer((ServerPlayer) player, debugPackage);


    }
}
