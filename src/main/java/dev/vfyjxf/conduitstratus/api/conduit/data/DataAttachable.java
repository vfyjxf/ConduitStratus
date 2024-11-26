package dev.vfyjxf.conduitstratus.api.conduit.data;

import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

@SuppressWarnings("ConstantConditions")
public interface DataAttachable {

    <T> void attach(DataKey<T> key, T value);

    <T> T get(DataKey<T> key);

    <T> T getOrDefault(DataKey<T> key, T defaultValue);

    default <T> T getOrDefault(DataKey<T> key, Supplier<T> supplier) {
        T value = get(key);
        if (value == null) {
            return supplier.get();
        }
        return value;
    }

    default <T> T getIfAbsentPut(DataKey<T> key, Supplier<T> supplier) {
        T value = get(key);
        if (value == null) {
            value = supplier.get();
            attach(key, value);
        }
        return value;
    }

    default <T> T getIfAbsentPut(DataKey<T> key, @Nullable T value) {
        T existing = get(key);
        if (existing == null) {
            attach(key, value);
            return value;
        }
        return existing;
    }

    <T> T detach(DataKey<T> key);

    void clear();

    boolean isEmpty();

    default boolean hasData() {
        return !isEmpty();
    }

    boolean has(DataKey<?> key);

}
