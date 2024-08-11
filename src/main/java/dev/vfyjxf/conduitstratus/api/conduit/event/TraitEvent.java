package dev.vfyjxf.conduitstratus.api.conduit.event;

import dev.vfyjxf.conduitstratus.api.conduit.trait.ConduitTrait;
import dev.vfyjxf.conduitstratus.api.event.IEventContext;
import net.minecraft.server.level.ServerLevel;

public interface TraitEvent extends ConduitEvent {


    interface OnTraitHandle extends TraitEvent {
        <TYPE extends ConduitTrait<TYPE>, TRAIT extends ConduitTrait<TYPE>>
        void onTraitHandle(IEventContext.Interruptible context, ServerLevel level, TRAIT trait);
    }

    interface OnTraitHandlePre extends TraitEvent {
        void onTraitHandlePre(IEventContext.Cancelable context);
    }

    interface OnTraitHandlePost extends TraitEvent {
        void onTraitHandlePost(IEventContext.Interruptible context);
    }

}
