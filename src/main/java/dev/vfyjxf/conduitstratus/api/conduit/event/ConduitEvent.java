package dev.vfyjxf.conduitstratus.api.conduit.event;

import dev.vfyjxf.conduitstratus.api.event.EventContext;
import dev.vfyjxf.conduitstratus.api.event.EventDefinition;
import dev.vfyjxf.conduitstratus.api.event.EventFactory;

public interface ConduitEvent {

    EventDefinition<OnNetworkTick> onNetworkTick = EventFactory.define(OnNetworkTick.class, listeners -> context -> {
        for (var listener : listeners) {
            listener.onNetworkTick(context);
            if (context.interrupted()) return;
        }
    });

    EventDefinition<OnTraitHandle> onTraitHandle = EventFactory.define(OnTraitHandle.class, listeners -> context -> {
        for (var listener : listeners) {
            listener.onTraitHandle(context);
            if (context.interrupted()) return;
        }
    });

    EventDefinition<OnTraitHandlePre> onTraitHandlePre = EventFactory.define(OnTraitHandlePre.class, listeners -> context -> {
        for (var listener : listeners) {
            listener.onTraitHandlePre(context);
            if (context.interrupted()) return;
        }
    });

    EventDefinition<OnTraitHandlePost> onTraitHandlePost = EventFactory.define(OnTraitHandlePost.class, listeners -> context -> {
        for (var listener : listeners) {
            listener.onTraitHandlePost(context);
            if (context.interrupted()) return;
        }
    });

    interface OnNetworkTick extends ConduitEvent {
        void onNetworkTick(EventContext.Cancelable context);

    }

    interface OnTraitHandle extends ConduitEvent {
        void onTraitHandle(EventContext.Interruptible context);
    }

    interface OnTraitHandlePre extends ConduitEvent {
        void onTraitHandlePre(EventContext.Cancelable context);

    }

    interface OnTraitHandlePost extends ConduitEvent {
        void onTraitHandlePost(EventContext.Interruptible context);
    }

}
