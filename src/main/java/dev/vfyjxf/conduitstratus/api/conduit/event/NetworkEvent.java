package dev.vfyjxf.conduitstratus.api.conduit.event;

import dev.vfyjxf.conduitstratus.api.conduit.data.NetworkData;
import dev.vfyjxf.conduitstratus.api.conduit.network.Network;
import dev.vfyjxf.conduitstratus.api.conduit.network.NetworkNode;
import dev.vfyjxf.conduitstratus.api.conduit.trait.Trait;
import dev.vfyjxf.conduitstratus.api.event.EventDefinition;
import dev.vfyjxf.conduitstratus.api.event.EventFactory;

import static dev.vfyjxf.conduitstratus.api.event.EventContext.Cancelable;
import static dev.vfyjxf.conduitstratus.api.event.EventContext.Common;


public interface NetworkEvent {

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

    EventDefinition<OnNetworkTick> onNetworkTick = EventFactory.define(OnNetworkTick.class, listeners -> (network, context) -> {
        for (var listener : listeners) {
            listener.onNetworkTick(network, context);
            if (context.interrupted()) return;
        }
    });

    EventDefinition<OnTraitAdded> onTraitAdded = EventFactory.define(OnTraitAdded.class, listeners -> (node, trait) -> {
        for (var listener : listeners) {
            listener.onTraitAdded(node, trait);
        }
    });


    interface OnNetworkTick extends NetworkEvent {
        void onNetworkTick(Network network, Cancelable context);
    }

    interface OnDataUpdate extends NetworkEvent {
        void onNetworkDataUpdate(NetworkNode node, NetworkData<?> data, Common context);
    }

    interface OnNodeAdded extends NetworkEvent {
        void onNodeAdded(NetworkNode node);
    }

    interface OnNodeRemoved extends NetworkEvent {
        void onNodeRemoved(NetworkNode node);
    }

    interface OnTraitAdded extends NetworkEvent {
        void onTraitAdded(NetworkNode node, Trait trait);
    }

}
