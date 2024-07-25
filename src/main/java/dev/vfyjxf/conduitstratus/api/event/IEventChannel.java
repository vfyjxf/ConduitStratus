package dev.vfyjxf.conduitstratus.api.event;

public interface IEventChannel<T> {

    IEventHandler<T> handler();

    default IEventContext.Common context() {
        return new IEventContext.Common(this);
    }

    default IEventContext.Cancelable cancelable() {
        return new IEventContext.Cancelable(this);
    }

    default IEventContext.Interruptible interruptible() {
        return new IEventContext.Interruptible(this);
    }

    default <E extends T> void register(IEventDefinition<E> definition, E listener) {
        get(definition).register(listener);
    }

    <E extends T> IEvent<E> get(IEventDefinition<E> definition);

    void clearAllListeners();

}
