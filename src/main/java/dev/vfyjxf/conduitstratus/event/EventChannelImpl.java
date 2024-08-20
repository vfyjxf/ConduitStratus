package dev.vfyjxf.conduitstratus.event;

import dev.vfyjxf.conduitstratus.api.event.Event;
import dev.vfyjxf.conduitstratus.api.event.EventDefinition;
import dev.vfyjxf.conduitstratus.api.event.EventHandler;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;

public class EventChannelImpl<T> implements dev.vfyjxf.conduitstratus.api.event.EventChannel<T> {

    private final EventHandler<T> handler;
    private final MutableMap<EventDefinition<?>, Event<?>> listeners = Maps.mutable.empty();
    private final MutableList<Checker<T>> events = Lists.mutable.empty();

    public EventChannelImpl(EventHandler<T> handler) {
        this.handler = handler;
    }

    @Override
    public EventHandler<T> handler() {
        return handler;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends T> Event<E> get(EventDefinition<E> definition) {
        if (events.noneSatisfy(checker -> checker.check(definition.type()))) {
            throw new IllegalArgumentException("Event type: " + definition.type() + " not allowed");
        }
        return (Event<E>) listeners.getIfAbsentPut(definition, definition::create);
    }

    @Override
    public void clearAllListeners() {
        for (Event<?> source : listeners) {
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
