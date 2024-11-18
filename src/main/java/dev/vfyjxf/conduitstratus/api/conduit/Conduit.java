package dev.vfyjxf.conduitstratus.api.conduit;

import dev.vfyjxf.conduitstratus.api.conduit.trait.Trait;
import dev.vfyjxf.conduitstratus.conduit.ConduitBlockItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

//TODO:rework this,Conduit拥有color信息还是ConduitItem拥有color信息?
//TODO:做好数据分割，明确哪些数据是ConduitItem的，哪些数据是Conduit的,哪些是ConduitTrait的
//TODO:颜色管道的模型，和颜色数据，和注册表.
//TODO:决定是否Codec化
public interface Conduit {

    @Nullable
    static Conduit fromItem(ItemStack stack) {
        if (stack.isEmpty()) return null;
        return stack.getItem() instanceof ConduitBlockItem blockItem ? blockItem.getConduit() : null;
    }

    default ConduitColor color() {
        return ConduitColor.BLACK;
    }

//    int maxTypeChannel();

    default boolean acceptsTrait(Trait trait) {
        return trait.attachable(this);
    }

    default boolean connectable(Conduit another) {
        return true;
    }

}
