package dev.vfyjxf.conduitstratus.api.event;

public interface IEventDefinition<T> {

    Class<T> type();

    IEvent<T> create();

    IEvent<T> global();

    default T invoker() {
        return global().invoker();
    }

    default T register(T listener) {
        return global().register(listener);
    }

    default void unregister(T listener) {
        global().unregister(listener);
    }

    default boolean isRegistered(T listener) {
        return global().isRegistered(listener);
    }

    default void unregisterAll() {
        global().clearListeners();
    }

}
