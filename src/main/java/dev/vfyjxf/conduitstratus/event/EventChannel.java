package dev.vfyjxf.conduitstratus.event;

import dev.vfyjxf.conduitstratus.api.event.IEvent;
import dev.vfyjxf.conduitstratus.api.event.IEventChannel;
import dev.vfyjxf.conduitstratus.api.event.IEventDefinition;
import dev.vfyjxf.conduitstratus.api.event.IEventHandler;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.map.MutableMap;

public class EventChannel<T> implements IEventChannel<T> {

    private final IEventHandler<T> handler;
    private final MutableMap<IEventDefinition<?>, IEvent<?>> listeners = Maps.mutable.empty();

    public EventChannel(IEventHandler<T> handler) {
        this.handler = handler;
    }

    @Override
    public IEventHandler<T> handler() {
        return handler;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends T> IEvent<E> get(IEventDefinition<E> definition) {
        return (IEvent<E>) listeners.getIfAbsentPut(definition, definition::create);
    }

    @Override
    public void clearAllListeners() {
        for (IEvent<?> source : listeners) {
            source.clearListeners();
        }
    }
}
