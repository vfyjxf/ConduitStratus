package dev.vfyjxf.conduitstratus.conduit.block;

import dev.vfyjxf.conduitstratus.conduit.blockentity.ConduitBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
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
        registerDefaultState(
                defaultBlockState()
                        .setValue(BlockStateProperties.WATERLOGGED, false)
        );
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ConduitBlockEntity(pos, state);
    }
}
