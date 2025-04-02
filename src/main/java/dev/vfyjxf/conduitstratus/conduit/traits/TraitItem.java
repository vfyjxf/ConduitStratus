package dev.vfyjxf.conduitstratus.conduit.traits;

import dev.vfyjxf.conduitstratus.api.conduit.TraitType;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import dev.vfyjxf.conduitstratus.conduit.ConnectionState;
import dev.vfyjxf.conduitstratus.conduit.blockentity.ConduitBlockEntity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

public class TraitItem extends Item {

    private final TraitType type;

    public TraitItem(TraitType type, Properties properties) {
        super(properties);
        this.type = type;
    }

    public TraitType getType() {
        return type;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var player = context.getPlayer();
        var level = context.getLevel();
        var pos = context.getClickedPos();
        var itemStack = context.getItemInHand();
        var side = context.getClickedFace();
        var traitType = itemStack.getItem() instanceof TraitItem traitItem ? traitItem.type : null;
        if (traitType == null) return InteractionResult.PASS;
        if (level.getBlockEntity(pos) instanceof ConduitBlockEntity blockEntity) {
            ConnectionState connectionState = blockEntity.getConnectionState();
            if (connectionState.hasTrait(side) || connectionState.hasConnection(side)) return InteractionResult.PASS;
            NetworkNode networkNode = blockEntity.networkNode();
            if (networkNode == null) return InteractionResult.PASS;
            networkNode.addTrait(side, type.factory().create(type, networkNode, side));
            blockEntity.addTrait(side);
            BlockState state = level.getBlockState(pos);
            SoundType ss = state.getSoundType(level, pos, player);
            level.playSound(null, pos, ss.getPlaceSound(), SoundSource.BLOCKS, (ss.getVolume() + 1.0F) / 2.0F,
                            ss.getPitch() * 0.8F
            );
            return InteractionResult.sidedSuccess(level.isClientSide());

        }

        return InteractionResult.PASS;
    }
}
