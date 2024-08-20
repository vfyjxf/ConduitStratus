package dev.vfyjxf.conduitstratus.api.event;

import org.eclipse.collections.impl.list.mutable.FastList;

import java.util.List;
import java.util.function.Function;

/**
 * <p>
 * Some basic promises:
 * <p>
 * 1. The naming define events follows the lowercase hump naming
 * <p>
 * 2. Pre-phase events do not need to add the Pre suffix, but Post-phase events need to add the Post suffix.
 * <p>
 * 3. Usually, an event is defined in an interface with its listeners.
 */
public final class EventFactory {


    private EventFactory() {
    }

    public static <T> EventDefinition<T> define(Class<T> type, Function<List<T>, T> invokerFactory) {
        return new EventDefinitionImpl<>(type, invokerFactory);
    }

    public static <T> Event<T> createEvent(Function<List<T>, T> invokerFactory) {
        return new EventImpl<>(invokerFactory);
    }

    private static class EventDefinitionImpl<T> implements EventDefinition<T> {

        private final Class<T> type;
        private final Function<List<T>, T> function;
        private final Event<T> global;

        private EventDefinitionImpl(Class<T> type, Function<List<T>, T> function) {
            this.type = type;
            this.function = function;
            this.global = new EventImpl<>(function);
        }

        @Override
        public Class<T> type() {
            return type;
        }

        @Override
        public Event<T> create() {
            return new EventImpl<>(function);
        }

        @Override
        public Event<T> global() {
            return global;
        }
    }

    private static class EventImpl<T> implements Event<T> {
        private final Function<List<T>, T> function;
        private T invoker = null;
        private final FastList<T> listeners;

        private EventImpl(Function<List<T>, T> function) {
            this.function = function;
            listeners = FastList.newList();
        }

        @Override
        public T invoker() {
            if (invoker == null) {
                update();
            }
            return invoker;
        }

        @Override
        public T register(T listener) {
            listeners.add(listener);
            invoker = null;
            return listener;
        }

        @Override
        public void unregister(T listener) {
            listeners.remove(listener);
            listeners.trimToSize();
            invoker = null;
        }

        @Override
        public boolean isRegistered(T listener) {
            return listeners.contains(listener);
        }

        @Override
        public void clearListeners() {
            listeners.clear();
            invoker = null;
        }

        public void update() {
            if (listeners.size() == 1) {
                invoker = listeners.getFirst();
            } else {
                invoker = function.apply(listeners);
            }
        }
    }
}
