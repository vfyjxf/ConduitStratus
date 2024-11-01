package dev.vfyjxf.conduitstratus.api.conduit.trait;

import dev.vfyjxf.conduitstratus.api.conduit.ConduitIO;
import dev.vfyjxf.conduitstratus.api.conduit.io.IORequest;

/**
 * @param <REQUEST> the type of request this trait can handle,it defines how a type of data can be transferred
 */
//TODO:定义标准管道网络客户端，即TransferTrait,定义Request和Response的行为与数据交换规则。
public interface TransferTrait<REQUEST extends IORequest, RESPONSE> extends ConduitTrait {

    @Override
    default TransferTrait<REQUEST, RESPONSE> setStatus(TraitStatus status) {
        return this;
    }

    @Override
    default TransferTrait<REQUEST, RESPONSE> setIO(ConduitIO conduitIO) {
        return this;
    }

    REQUEST sendRequest();

    RESPONSE handleRequest(REQUEST request);
}
