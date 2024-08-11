package dev.vfyjxf.conduitstratus.event;

import dev.vfyjxf.conduitstratus.api.event.IEvent;
import dev.vfyjxf.conduitstratus.api.event.IEventChannel;
import dev.vfyjxf.conduitstratus.api.event.IEventDefinition;
import dev.vfyjxf.conduitstratus.api.event.IEventHandler;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;

public class EventChannel<T> implements IEventChannel<T> {

    private final IEventHandler<T> handler;
    private final MutableMap<IEventDefinition<?>, IEvent<?>> listeners = Maps.mutable.empty();
    private final MutableList<Checker<T>> events = Lists.mutable.empty();

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
        if (events.noneSatisfy(checker -> checker.check(definition.type()))) {
            throw new IllegalArgumentException("Event type: " + definition.type() + " not allowed");
        }
        return (IEvent<E>) listeners.getIfAbsentPut(definition, definition::create);
    }

    @Override
    public void clearAllListeners() {
        for (IEvent<?> source : listeners) {
            source.clearListeners();
        }
    }

    @Override
    public void checkEvent(Checker<T> checker) {
        if (events.contains(checker)) {
            throw new IllegalArgumentException("Event checker already registered");
        }
        events.add(checker);
    }
}
