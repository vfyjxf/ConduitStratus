package dev.vfyjxf.conduitstratus.conduit.data;

import dev.vfyjxf.conduitstratus.api.conduit.data.INetworkData;
import dev.vfyjxf.conduitstratus.api.conduit.data.NetworkDataType;
import dev.vfyjxf.conduitstratus.api.conduit.event.IConduitNetworkEvent;
import dev.vfyjxf.conduitstratus.api.conduit.network.INetwork;
import dev.vfyjxf.conduitstratus.api.conduit.network.INetworkNode;

import java.util.Objects;

public class NetworkData<T> implements INetworkData<T> {

    private final INetworkNode node;
    private final NetworkDataType<T> type;
    private T value;

    public NetworkData(INetworkNode node, NetworkDataType<T> type, T value) {
        this.node = node;
        this.type = type;
        this.value = value;
    }

    @Override
    public NetworkDataType<T> type() {
        return type;
    }

    @Override
    public T directAccess() {
        return value;
    }

    @Override
    public T copy() {
        return type.copyFunction().apply(value);
    }

    @Override
    public void set(T value) {
        INetwork network = this.node.getNetwork();
        var context = network.common();
        network.listeners(IConduitNetworkEvent.onDataUpdate).onNetworkDataUpdate(node, this, context);
        if (context.cancelled()) return;

        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NetworkData<?> that = (NetworkData<?>) o;
        return node.equals(that.node) && type.equals(that.type) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        int result = node.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + Objects.hashCode(value);
        return result;
    }

    @Override
    public String toString() {
        return "NetworkData{" +
                "node=" + node +
                ", type=" + type +
                ", value=" + value +
                '}';
    }
}
