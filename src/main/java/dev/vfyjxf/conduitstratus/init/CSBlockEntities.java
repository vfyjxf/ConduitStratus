package dev.vfyjxf.conduitstratus.init;

import dev.vfyjxf.conduitstratus.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CSBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Constants.MOD_ID);


    private CSBlockEntities() {
    }

}
