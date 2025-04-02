package dev.vfyjxf.conduitstratus.conduit.data;

import dev.vfyjxf.conduitstratus.api.conduit.data.NetworkData;
import dev.vfyjxf.conduitstratus.api.conduit.data.NetworkDataType;
import dev.vfyjxf.conduitstratus.api.conduit.event.NetworkEvent;
import dev.vfyjxf.conduitstratus.api.conduit.network.Network;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;

import java.util.Objects;

public class ConduitNetworkData<T> implements NetworkData<T> {

    private final NetworkNode node;
    private final NetworkDataType<T> type;
    private T value;

    public ConduitNetworkData(NetworkNode node, NetworkDataType<T> type, T value) {
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
        Network network = this.node.getNetwork();
        var context = network.common();
        network.listeners(NetworkEvent.onDataUpdate).onNetworkDataUpdate(node, this, context);
        if (context.cancelled()) return;

        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConduitNetworkData<?> that = (ConduitNetworkData<?>) o;
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
        return "ConduitNetworkData{" +
                "node=" + node +
                ", type=" + type +
                ", value=" + value +
                '}';
    }
}
