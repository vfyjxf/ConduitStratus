package dev.vfyjxf.conduitstratus.conduit.data;

import dev.vfyjxf.conduitstratus.api.conduit.data.INetworkContext;
import dev.vfyjxf.conduitstratus.api.conduit.data.NetworkContextType;
import dev.vfyjxf.conduitstratus.api.conduit.event.IConduitNetworkEvent;
import dev.vfyjxf.conduitstratus.api.conduit.network.INetwork;

import java.util.Objects;

public class NetworkContext<T> implements INetworkContext<T> {

    private final INetwork network;
    private final NetworkContextType<T> type;
    private T value;

    public NetworkContext(INetwork network, NetworkContextType<T> type, T value) {
        this.network = network;
        this.type = type;
        this.value = value;
    }

    @Override
    public NetworkContextType<T> type() {
        return type;
    }

    @Override
    public T directAccess() {
        return value;
    }

    @Override
    public T mergeWith(T other) {
        return type.mergeFunction().apply(value, other);
    }

    @Override
    public T copy() {
        var copyFunction = type.copyFunction();
        if (copyFunction != null) return copyFunction.apply(value);
        else return null;
    }

    @Override
    public void set(T value) {
        var context = network.common();
        network.listeners(IConduitNetworkEvent.onContextUpdate).onNetworkContextUpdate(network, this, context);
        if (context.cancelled()) return;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NetworkContext<?> that = (NetworkContext<?>) o;
        return network.equals(that.network) && type.equals(that.type) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        int result = network.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + Objects.hashCode(value);
        return result;
    }

    @Override
    public String toString() {
        return "NetworkContext{" +
                "network=" + network +
                ", type=" + type +
                ", value=" + value +
                '}';
    }
}
