package dev.vfyjxf.conduitstratus.api.conduit;

import dev.vfyjxf.conduitstratus.api.conduit.trait.ConduitTrait;

//TODO:rework this,Conduit拥有color信息还是ConduitItem拥有color信息?
//TODO:做好数据分割，明确哪些数据是ConduitItem的，哪些数据是Conduit的,哪些是ConduitTrait的
//TODO:颜色管道的模型，和颜色数据，和注册表.
//TODO:决定是否Codec化
public interface Conduit {

//    ConduitColor color();

//    int maxTypeChannel();

    default boolean acceptsTrait(ConduitTrait trait) {
        return trait.attachable(this);
    }

    default boolean connectable(Conduit another) {
        return true;
    }

}
