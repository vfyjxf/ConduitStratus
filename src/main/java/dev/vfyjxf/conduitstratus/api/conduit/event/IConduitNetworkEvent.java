package dev.vfyjxf.conduitstratus.api.conduit.event;

import dev.vfyjxf.conduitstratus.api.conduit.data.INetworkContext;
import dev.vfyjxf.conduitstratus.api.conduit.data.INetworkData;
import dev.vfyjxf.conduitstratus.api.conduit.network.INetwork;
import dev.vfyjxf.conduitstratus.api.conduit.network.INetworkNode;
import dev.vfyjxf.conduitstratus.api.event.EventFactory;
import dev.vfyjxf.conduitstratus.api.event.IEventDefinition;

import static dev.vfyjxf.conduitstratus.api.event.IEventContext.*;

public interface IConduitNetworkEvent extends IConduitEvent {

    IEventDefinition<OnContextUpdate> onContextUpdate = EventFactory.define(OnContextUpdate.class, listeners -> ((network, networkContext, context) -> {
        for (OnContextUpdate listener : listeners) {
            listener.onNetworkContextUpdate(network, networkContext, context);
            if (context.interrupted()) return;
        }
    }));

    IEventDefinition<OnDataUpdate> onDataUpdate = EventFactory.define(OnDataUpdate.class, listeners -> ((node, data, context) -> {
        for (OnDataUpdate listener : listeners) {
            listener.onNetworkDataUpdate(node, data, context);
            if (context.interrupted()) return;
        }
    }));

    interface OnContextUpdate extends IConduitNetworkEvent {

        void onNetworkContextUpdate(INetwork network, INetworkContext<?> networkContext, Common context);

    }

    interface OnDataUpdate extends IConduitNetworkEvent {

        void onNetworkDataUpdate(INetworkNode node, INetworkData<?> data, Common context);

    }

}
