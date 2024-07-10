package dev.vfyjxf.conduitstratus.api.event;

public interface IEventChannel<E extends IEventHandler<E>> {

    IEventHandler<E> handler();

    default IEventContext.Common context(){
        return new IEventContext.Common(this);
    }

    default IEventContext.Cancelable cancelable(){
        return new IEventContext.Cancelable(this);
    }

    default IEventContext.Interruptible interruptible(){
        return new IEventContext.Interruptible(this);
    }

    default <T> void register(IEventDefinition<T> definition, T listener) {
        get(definition).register(listener);
    }

    <T> IEvent<T> get(IEventDefinition<T> definition);

    void clearAllListeners();

}
