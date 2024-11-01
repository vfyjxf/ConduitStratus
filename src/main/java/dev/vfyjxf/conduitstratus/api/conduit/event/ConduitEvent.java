package dev.vfyjxf.conduitstratus.api.conduit.event;

import dev.vfyjxf.conduitstratus.api.conduit.network.Network;
import dev.vfyjxf.conduitstratus.api.event.EventContext;
import dev.vfyjxf.conduitstratus.api.event.EventDefinition;
import dev.vfyjxf.conduitstratus.api.event.EventFactory;

public interface ConduitEvent {

    EventDefinition<OnNetworkTick> onNetworkTick = EventFactory.define(OnNetworkTick.class, listeners -> (network, context) -> {
        for (var listener : listeners) {
            listener.onNetworkTick(network, context);
            if (context.interrupted()) return;
        }
    });

    interface OnNetworkTick extends ConduitEvent {
        void onNetworkTick(Network network, EventContext.Cancelable context);
    }

}
