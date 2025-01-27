package dev.vfyjxf.conduitstratus.api.conduit.trait;

import dev.vfyjxf.conduitstratus.api.conduit.TraitIO;
import dev.vfyjxf.conduitstratus.api.conduit.TickStatus;
import dev.vfyjxf.conduitstratus.api.conduit.io.IORequest;

/**
 * @param <REQUEST> the type of request this trait can handle,it defines how a type of data can be transferred
 */
//TODO:定义标准管道网络客户端，即TransferTrait,定义Request和Response的行为与数据交换规则。
//TODO:更好的io交互api设计
//1.要保证个体的独立性，允许个体拒绝来自网络的io请求
//2.尊重网络的权利，允许网络管理整个网络的io调度
//3.允许个体的特权，个体可以强制进行某些io推送，但其他个体可以拒绝这些推送，网络也可以拒绝这些推送
public interface TransferTrait<REQUEST extends IORequest, RESPONSE> extends Trait {

    @Override
    default TransferTrait<REQUEST, RESPONSE> setStatus(TickStatus status) {
        return this;
    }

    @Override
    default TransferTrait<REQUEST, RESPONSE> setIO(TraitIO traitIO) {
        return this;
    }

    boolean hasRequest();

    REQUEST sendRequest();

    RESPONSE handleRequest(REQUEST request);
}
