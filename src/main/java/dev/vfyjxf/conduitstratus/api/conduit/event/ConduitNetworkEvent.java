package dev.vfyjxf.conduitstratus.api.conduit.event;

import dev.vfyjxf.conduitstratus.api.conduit.data.NetworkData;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import dev.vfyjxf.conduitstratus.api.event.EventFactory;
import dev.vfyjxf.conduitstratus.api.event.IEventDefinition;

import static dev.vfyjxf.conduitstratus.api.event.IEventContext.Common;

public interface ConduitNetworkEvent extends ConduitEvent {

    IEventDefinition<OnDataUpdate> onDataUpdate = EventFactory.define(OnDataUpdate.class, listeners -> ((node, data, context) -> {
        for (OnDataUpdate listener : listeners) {
            listener.onNetworkDataUpdate(node, data, context);
            if (context.interrupted()) return;
        }
    }));

    IEventDefinition<OnNodeAdded> onNodeAdded = EventFactory.define(OnNodeAdded.class, listeners -> (node -> {
        for (OnNodeAdded listener : listeners) {
            listener.onNodeAdded(node);
        }
    }));

    IEventDefinition<OnNodeRemoved> onNodeRemoved = EventFactory.define(OnNodeRemoved.class, listeners -> (node -> {
        for (OnNodeRemoved listener : listeners) {
            listener.onNodeRemoved(node);
        }
    }));

    interface OnDataUpdate extends ConduitNetworkEvent {
        void onNetworkDataUpdate(NetworkNode node, NetworkData<?> data, Common context);
    }

    interface OnNodeAdded extends ConduitNetworkEvent {
            void onNodeAdded(NetworkNode node);
    }

    interface OnNodeRemoved extends ConduitNetworkEvent {
            void onNodeRemoved(NetworkNode node);
    }

}
