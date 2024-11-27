package dev.vfyjxf.conduitstratus.api.conduit.event;

import dev.vfyjxf.cloudlib.api.event.EventBase;
import dev.vfyjxf.cloudlib.api.event.EventDefinition;
import dev.vfyjxf.cloudlib.api.event.EventFactory;
import dev.vfyjxf.conduitstratus.api.conduit.trait.TransferTrait;
import dev.vfyjxf.conduitstratus.conduit.traits.item.ItemRequest;
import dev.vfyjxf.conduitstratus.conduit.traits.item.ItemResponse;

import static dev.vfyjxf.cloudlib.api.event.EventContext.*;


@EventBase
public interface TraitEvent {

    EventDefinition<OnTraitTick<TransferTrait<ItemRequest, ItemResponse>>> onItemTraitTick = EventFactory.defineGeneric(
            OnTraitTick.class,
            listeners -> (trait, context) -> {
                for (OnTraitTick<TransferTrait<ItemRequest, ItemResponse>> listener : listeners) {
                    listener.onTick(trait, context);
                }
            }
    );

    interface OnTraitTick<T extends TransferTrait<?, ?>> extends TraitEvent {
        void onTick(T trait, Cancelable context);
    }

}
