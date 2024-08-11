package dev.vfyjxf.conduitstratus.api.conduit.event;

import dev.vfyjxf.conduitstratus.api.conduit.data.NetworkData;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import dev.vfyjxf.conduitstratus.api.event.EventFactory;
import dev.vfyjxf.conduitstratus.api.event.IEventDefinition;

import static dev.vfyjxf.conduitstratus.api.event.IEventContext.*;

public interface ConduitNetworkEvent extends ConduitEvent {

    IEventDefinition<OnDataUpdate> onDataUpdate = EventFactory.define(OnDataUpdate.class, listeners -> ((node, data, context) -> {
        for (OnDataUpdate listener : listeners) {
            listener.onNetworkDataUpdate(node, data, context);
            if (context.interrupted()) return;
        }
    }));

    interface OnDataUpdate extends ConduitNetworkEvent {

        void onNetworkDataUpdate(NetworkNode node, NetworkData<?> data, Common context);

    }

}
