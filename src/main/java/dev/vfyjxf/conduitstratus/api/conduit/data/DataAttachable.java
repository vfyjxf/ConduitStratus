package dev.vfyjxf.conduitstratus.api.conduit.data;

import org.jetbrains.annotations.Nullable;

public interface DataAttachable {

    <T> void attach(DataKey<T> key, @Nullable T value);

    @Nullable
    <T> T get(DataKey<T> key);

    <T> T getOr(DataKey<T> key, T defaultValue);

    @Nullable
    <T> T detach(DataKey<T> key);

    void clear();

    boolean isEmpty();

    default boolean hasData() {
        return !isEmpty();
    }

    boolean has(DataKey<?> key);

}
