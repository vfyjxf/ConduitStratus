package dev.vfyjxf.conduitstratus.api.conduit.event;

import dev.vfyjxf.conduitstratus.api.conduit.trait.ConduitTrait;
import dev.vfyjxf.conduitstratus.api.event.EventContext;
import dev.vfyjxf.conduitstratus.api.event.EventDefinition;
import dev.vfyjxf.conduitstratus.api.event.EventFactory;
import net.minecraft.server.level.ServerLevel;

public interface TraitEvent extends ConduitEvent {

    EventDefinition<OnTraitHandle> onTraitHandle = EventFactory.define(OnTraitHandle.class, listeners -> (context, level, trait) -> {
        for (var listener : listeners) {
            listener.onTraitHandle(context, level, trait);
            if (context.interrupted()) return;
        }
    });

    EventDefinition<OnTraitHandlePre> onTraitHandlePre = EventFactory.define(OnTraitHandlePre.class, listeners -> (context, level, trait) -> {
        for (var listener : listeners) {
            listener.onTraitHandlePre(context, level, trait);
            if (context.interrupted()) return;
        }
    });

    EventDefinition<OnTraitHandlePost> onTraitHandlePost = EventFactory.define(OnTraitHandlePost.class, listeners -> (context, level, trait) -> {
        for (var listener : listeners) {
            listener.onTraitHandlePost(context, level, trait);
            if (context.interrupted()) return;
        }
    });

    interface OnTraitHandle extends TraitEvent {
        void onTraitHandle(EventContext.Interruptible context, ServerLevel level, ConduitTrait<? extends ConduitTrait<?>> trait);
    }

    interface OnTraitHandlePre extends TraitEvent {
        void onTraitHandlePre(EventContext.Cancelable context, ServerLevel level, ConduitTrait<? extends ConduitTrait<?>> trait);
    }

    interface OnTraitHandlePost extends TraitEvent {
        void onTraitHandlePost(EventContext.Interruptible context, ServerLevel level, ConduitTrait<? extends ConduitTrait<?>> trait);
    }

}
