package dev.vfyjxf.conduitstratus.api.event;


public interface EventHandler<T> {

    EventChannel<T> events();

    default EventContext.Common common() {
        return events().context();
    }

    default EventContext.Cancelable cancelable() {
        return events().cancelable();
    }

    default EventContext.Interruptible interruptible() {
        return events().interruptible();
    }

    default <E extends T> E listeners(EventDefinition<E> definition) {
        return events().get(definition).invoker();
    }

    default <E extends T> E register(EventDefinition<E> definition, E listener) {
        return events().get(definition).register(listener);
    }

    default <E extends T> void unregister(EventDefinition<E> definition, E listener) {
        events().get(definition).unregister(listener);
    }

    default <E extends T> void clear(EventDefinition<E> definition) {
        events().get(definition).clearListeners();
    }

    default void clearAll() {
        events().clearAllListeners();
    }

}
