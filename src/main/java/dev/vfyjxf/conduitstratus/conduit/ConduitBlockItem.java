package dev.vfyjxf.conduitstratus.conduit;

import dev.vfyjxf.conduitstratus.api.conduit.Conduit;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

public class ConduitBlockItem extends BlockItem {

    private final Conduit conduit;

    public ConduitBlockItem(Block block, Properties properties, Conduit conduit) {
        super(block, properties);
        this.conduit = conduit;
    }
}
