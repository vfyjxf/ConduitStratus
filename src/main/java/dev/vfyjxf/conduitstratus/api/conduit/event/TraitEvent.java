package dev.vfyjxf.conduitstratus.api.conduit.event;

import dev.vfyjxf.cloudlib.api.event.EventBase;
import dev.vfyjxf.cloudlib.api.event.EventDefinition;
import dev.vfyjxf.cloudlib.api.event.EventFactory;
import dev.vfyjxf.conduitstratus.api.conduit.trait.TransferTrait;
import dev.vfyjxf.conduitstratus.conduit.traits.fluid.FluidRequest;
import dev.vfyjxf.conduitstratus.conduit.traits.fluid.FluidResponse;
import dev.vfyjxf.conduitstratus.conduit.traits.item.ItemRequest;
import dev.vfyjxf.conduitstratus.conduit.traits.item.ItemResponse;

import static dev.vfyjxf.cloudlib.api.event.EventContext.Cancelable;


@EventBase
public interface TraitEvent {

    EventDefinition<OnItemTraitTick> onItemTraitTick = EventFactory.define(
            OnItemTraitTick.class,
            listeners -> (trait, context) -> {
                for (var listener : listeners) {
                    listener.onTick(trait, context);
                }
            }
    );

    EventDefinition<OnFluidTraitTick> onFluidTraitTick = EventFactory.define(
            OnFluidTraitTick.class,
            listeners -> (trait, context) -> {
                for (var listener : listeners) {
                    listener.onTick(trait, context);
                }
            }
    );


    interface OnTraitTick<T extends TransferTrait<?, ?>> extends TraitEvent {
        void onTick(T trait, Cancelable context);
    }

    interface OnItemTraitTick extends OnTraitTick<TransferTrait<ItemRequest, ItemResponse>> {
    }

    interface OnFluidTraitTick extends OnTraitTick<TransferTrait<FluidRequest, FluidResponse>> {
    }

}
