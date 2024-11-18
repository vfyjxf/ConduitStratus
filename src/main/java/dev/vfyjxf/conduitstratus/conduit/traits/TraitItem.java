package dev.vfyjxf.conduitstratus.conduit.traits;

import dev.vfyjxf.conduitstratus.api.conduit.trait.TraitType;
import dev.vfyjxf.conduitstratus.conduit.blockentity.ConduitBlockEntity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

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
            if (!blockEntity.getConnections().hasConnection(side)) {
                blockEntity.addTrait(traitType, side);
                var state = level.getBlockState(pos);
                var ss = state.getSoundType(level, pos, player);
                level.playSound(null, pos, ss.getPlaceSound(), SoundSource.BLOCKS, (ss.getVolume() + 1.0F) / 2.0F,
                                ss.getPitch() * 0.8F);
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
        }

        return InteractionResult.PASS;
    }
}
