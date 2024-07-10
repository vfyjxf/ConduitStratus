package dev.vfyjxf.conduitstratus.api.event;


public interface IEventHandler<E extends IEventHandler<E>> {

    IEventChannel<E> channel();

    default IEventContext.Common common() {
        return channel().context();
    }

    default IEventContext.Cancelable cancelable() {
        return channel().cancelable();
    }

    default IEventContext.Interruptible interruptible() {
        return channel().interruptible();
    }

    default <T> T listeners(IEventDefinition<T> definition) {
        return channel().get(definition).invoker();
    }


    default <T> T register(IEventDefinition<T> definition, T listener) {
        return channel().get(definition).register(listener);
    }

    default <T> void unregister(IEventDefinition<T> definition, T listener) {
        channel().get(definition).unregister(listener);
    }

    default <T> void clear(IEventDefinition<T> definition) {
        channel().get(definition).clearListeners();
    }

    default void clearAll() {
        channel().clearAllListeners();
    }

}
