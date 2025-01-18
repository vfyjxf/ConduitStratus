package dev.vfyjxf.conduitstratus.conduit.block;

import dev.vfyjxf.conduitstratus.conduit.ConduitBlockItem;
import dev.vfyjxf.conduitstratus.conduit.blockentity.ConduitBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ConduitBlock extends Block implements EntityBlock, SimpleWaterloggedBlock {

    public ConduitBlock() {
        super(
                Properties.of()
                        .mapColor(MapColor.NONE)
                        .sound(SoundType.GLASS)
                        .noOcclusion()
                        .noLootTable()
                        .dynamicShape()
                        .forceSolidOn()
                        .lightLevel((state) -> 0)
                        .strength(1.5F, 18F)
        );
    }


    @Override
    protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ConduitBlockEntity(pos, state);
    }

    @Override
    protected BlockState updateShape(
            BlockState oldState,
            Direction direction,
            BlockState neighborState,
            LevelAccessor level,
            BlockPos pos,
            BlockPos neighborPos
    ) {
        if (level.getBlockEntity(pos) instanceof ConduitBlockEntity conduitBlockEntity) {
            conduitBlockEntity.refreshNeighbor();
        }
        return super.updateShape(oldState, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if(!level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ConduitBlockEntity conduitBlockEntity) {
                conduitBlockEntity.refreshNeighbor();
            }
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if(!level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ConduitBlockEntity conduitBlockEntity) {
                conduitBlockEntity.onDestroyed();
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (level.getBlockEntity(pos) instanceof ConduitBlockEntity blockEntity) {
            var conduit = stack.getItem() instanceof ConduitBlockItem blockItem ? blockItem.getConduit() : null;
            if (conduit != null) {
                blockEntity.setConduit(conduit);
            }
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(
            ItemStack stack,
            BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hitResult
    ) {
        //TODO:apply traits to the conduit
        if (level.getBlockEntity(pos) instanceof ConduitBlockEntity blockEntity) {

        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof ConduitBlockEntity conduitEntity) {
            return conduitEntity.getShape();
        }
        return Shapes.empty();
    }
}
