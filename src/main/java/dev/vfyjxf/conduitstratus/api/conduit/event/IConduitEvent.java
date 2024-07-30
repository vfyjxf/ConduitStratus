package dev.vfyjxf.conduitstratus.api.conduit.event;

import dev.vfyjxf.conduitstratus.api.event.EventFactory;
import dev.vfyjxf.conduitstratus.api.event.IEventContext;
import dev.vfyjxf.conduitstratus.api.event.IEventDefinition;

public interface IConduitEvent {

    IEventDefinition<OnNetworkTick> onNetworkTick = EventFactory.define(OnNetworkTick.class, listeners -> context -> {
        for (var listener : listeners) {
            listener.onNetworkTick(context);
            if (context.interrupted()) return;
        }
    });

    IEventDefinition<OnTraitHandle> onTraitHandle = EventFactory.define(OnTraitHandle.class, listeners -> context -> {
        for (var listener : listeners) {
            listener.onTraitHandle(context);
            if (context.interrupted()) return;
        }
    });

    IEventDefinition<OnTraitHandlePre> onTraitHandlePre = EventFactory.define(OnTraitHandlePre.class, listeners -> context -> {
        for (var listener : listeners) {
            listener.onTraitHandlePre(context);
            if (context.interrupted()) return;
        }
    });

    IEventDefinition<OnTraitHandlePost> onTraitHandlePost = EventFactory.define(OnTraitHandlePost.class, listeners -> context -> {
        for (var listener : listeners) {
            listener.onTraitHandlePost(context);
            if (context.interrupted()) return;
        }
    });

    interface OnNetworkTick extends IConduitEvent {
        void onNetworkTick(IEventContext.Cancelable context);

    }

    interface OnTraitHandle extends IConduitEvent {
        void onTraitHandle(IEventContext.Interruptible context);
    }

    interface OnTraitHandlePre extends IConduitEvent {
        void onTraitHandlePre(IEventContext.Cancelable context);

    }

    interface OnTraitHandlePost extends IConduitEvent {
        void onTraitHandlePost(IEventContext.Interruptible context);
    }

}