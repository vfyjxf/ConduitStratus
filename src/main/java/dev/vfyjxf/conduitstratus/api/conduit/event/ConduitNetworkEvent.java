package dev.vfyjxf.conduitstratus.api.conduit.event;

import dev.vfyjxf.conduitstratus.api.conduit.data.NetworkData;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import dev.vfyjxf.conduitstratus.api.event.EventDefinition;
import dev.vfyjxf.conduitstratus.api.event.EventFactory;

import static dev.vfyjxf.conduitstratus.api.event.EventContext.Common;


public interface ConduitNetworkEvent extends ConduitEvent {

    EventDefinition<OnDataUpdate> onDataUpdate = EventFactory.define(OnDataUpdate.class, listeners -> ((node, data, context) -> {
        for (OnDataUpdate listener : listeners) {
            listener.onNetworkDataUpdate(node, data, context);
            if (context.interrupted()) return;
        }
    }));

    EventDefinition<OnNodeAdded> onNodeAdded = EventFactory.define(OnNodeAdded.class, listeners -> (node -> {
        for (OnNodeAdded listener : listeners) {
            listener.onNodeAdded(node);
        }
    }));

    EventDefinition<OnNodeRemoved> onNodeRemoved = EventFactory.define(OnNodeRemoved.class, listeners -> (node -> {
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
