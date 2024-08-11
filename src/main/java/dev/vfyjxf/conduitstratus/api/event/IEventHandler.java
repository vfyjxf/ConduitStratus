package dev.vfyjxf.conduitstratus.api.event;


public interface IEventHandler<T> {

    IEventChannel<T> channel();

    default IEventContext.Common common() {
        return channel().context();
    }

    default IEventContext.Cancelable cancelable() {
        return channel().cancelable();
    }

    default IEventContext.Interruptible interruptible() {
        return channel().interruptible();
    }

    default <E extends T> E listeners(IEventDefinition<E> definition) {
        return channel().get(definition).invoker();
    }

    default <E extends T> E register(IEventDefinition<E> definition, E listener) {
        return channel().get(definition).register(listener);
    }

    default <E extends T> void unregister(IEventDefinition<E> definition, E listener) {
        channel().get(definition).unregister(listener);
    }

    default <E extends T> void clear(IEventDefinition<E> definition) {
        channel().get(definition).clearListeners();
    }

    default void clearAll() {
        channel().clearAllListeners();
    }

}
