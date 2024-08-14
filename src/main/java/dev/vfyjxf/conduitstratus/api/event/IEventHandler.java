package dev.vfyjxf.conduitstratus.api.event;


public interface IEventHandler<T> {

    IEventChannel<T> events();

    default IEventContext.Common common() {
        return events().context();
    }

    default IEventContext.Cancelable cancelable() {
        return events().cancelable();
    }

    default IEventContext.Interruptible interruptible() {
        return events().interruptible();
    }

    default <E extends T> E listeners(IEventDefinition<E> definition) {
        return events().get(definition).invoker();
    }

    default <E extends T> E register(IEventDefinition<E> definition, E listener) {
        return events().get(definition).register(listener);
    }

    default <E extends T> void unregister(IEventDefinition<E> definition, E listener) {
        events().get(definition).unregister(listener);
    }

    default <E extends T> void clear(IEventDefinition<E> definition) {
        events().get(definition).clearListeners();
    }

    default void clearAll() {
        events().clearAllListeners();
    }

}
