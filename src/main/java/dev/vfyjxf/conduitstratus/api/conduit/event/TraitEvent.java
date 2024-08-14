package dev.vfyjxf.conduitstratus.api.conduit.event;

import dev.vfyjxf.conduitstratus.api.conduit.trait.ConduitTrait;
import dev.vfyjxf.conduitstratus.api.event.EventFactory;
import dev.vfyjxf.conduitstratus.api.event.IEventContext;
import dev.vfyjxf.conduitstratus.api.event.IEventDefinition;
import net.minecraft.server.level.ServerLevel;

public interface TraitEvent extends ConduitEvent {

    IEventDefinition<OnTraitHandle> onTraitHandle = EventFactory.define(OnTraitHandle.class, listeners -> (context, level, trait) -> {
        for (var listener : listeners) {
            listener.onTraitHandle(context, level, trait);
            if (context.interrupted()) return;
        }
    });

    IEventDefinition<OnTraitHandlePre> onTraitHandlePre = EventFactory.define(OnTraitHandlePre.class, listeners -> (context, level, trait) -> {
        for (var listener : listeners) {
            listener.onTraitHandlePre(context, level, trait);
            if (context.interrupted()) return;
        }
    });

    IEventDefinition<OnTraitHandlePost> onTraitHandlePost = EventFactory.define(OnTraitHandlePost.class, listeners -> (context, level, trait) -> {
        for (var listener : listeners) {
            listener.onTraitHandlePost(context, level, trait);
            if (context.interrupted()) return;
        }
    });

    interface OnTraitHandle extends TraitEvent {
        void onTraitHandle(IEventContext.Interruptible context, ServerLevel level, ConduitTrait<? extends ConduitTrait<?>> trait);
    }

    interface OnTraitHandlePre extends TraitEvent {
        void onTraitHandlePre(IEventContext.Cancelable context, ServerLevel level, ConduitTrait<? extends ConduitTrait<?>> trait);
    }

    interface OnTraitHandlePost extends TraitEvent {
        void onTraitHandlePost(IEventContext.Interruptible context, ServerLevel level, ConduitTrait<? extends ConduitTrait<?>> trait);
    }

}
